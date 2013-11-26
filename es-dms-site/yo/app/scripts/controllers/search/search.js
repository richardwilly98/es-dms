'use strict';

esDmsSiteApp.controller('SearchCtrl', ['$scope',
  function ($scope) {
    $scope.model = {};
}]);


esDmsSiteApp.controller('SearchResultCtrl', ['$log', '$scope', '$modal', 'documentService', 'searchService', 'sharedService', 'messagingService',
  function ($log, $scope, $modal, documentService, searchService, sharedService, messagingService) {

  // $scope.documents = [];
  // $scope.facets = [];
  // $scope.totalHits = 0;
  // $scope.elapsedTime = 0;
  $scope.maxPages = 10;
  $scope.totalPages = 0;
  $scope.currentPage = 1;
  $scope.newtag = {};
  $scope.terms = [];
  $scope.service = sharedService;
  $scope.pageSize = $scope.service.getSettings().user.pageSize;
  // $scope.model = {
  //   // criteria: null
  // };
  
  // Define facet settings. It is recommended to keep at least 'Tags' facet
  $scope.facetSettings = [
    {'name' : 'Tags', terms: [{'field': 'tags', 'size': 10}]},
    {'name' : 'Language', terms: [{'field': 'versions.file.content.language.lang', 'size': 10}]},
    {'name' : 'Author', terms: [{'field': 'attributes.author', 'size': 10}]}
  ];

  $scope.$watch('service.getUserSettings()',
    function(newValue) {
      $log.log('watch - pageSize: ' + newValue.pageSize);
      if (newValue && $scope.pageSize !== newValue.pageSize) {
        $scope.pageSize = newValue.pageSize;
        $scope.search();
      }
    });

  $scope.search = function(criteria) {
		$log.log('search - ' + criteria);
		if (criteria === undefined || criteria === '' || criteria === '*') {
      messagingService.push({'type': 'info', 'title': 'Search', 'content': 'Empty or wildcard not allowed', 'timeout': 5000});
      //$scope.totalHits = 0;
      $scope.totalPages = 0;
      $scope.currentPage = 1;
			// $scope.documents = [];
      $scope.result = {};
      $scope.terms = [];
      //$scope.facets = [];
      //$scope.model.criteria = $scope.criteria;
      // searchService.criteria(null);
		} else {
      // $scope.documents = [];
      $scope.result = {};
      $scope.terms = [];
      //$scope.facets = [];
      //$scope.model.criteria = $scope.criteria;
      // searchService.criteria($scope.criteria);
			find(0, criteria, true);
		}
  };

  function find(first, criteria, updatePagination) {
		var filters = getFilters();
    $log.log('About to execute facetedSearch with filter: ' + JSON.stringify(filters));
    searchService.facetedSearch(first, $scope.pageSize, criteria, $scope.facetSettings, filters);
  }
  
  $scope.$on('search:execute', function(evt, args) {
    if (args.criteria !== undefined) {
      $scope.criteria = args.criteria;
      $scope.search(args.criteria);
    }
  });

  $scope.$on('facetedSearch:result', function(evt, args) {
    $log.log('SearchResultCtrl - facetedSearch:result');
    $scope.result = args;
    if ($scope.result.totalHits === 0) {
      $scope.totalPages = 0;
      $scope.currentPage = 1;
      $scope.result = {};
      $scope.terms = [];
      if (getFilters() !== {}) {
        messagingService.push({'type': 'warning', 'title': 'Search', 'content': 'No document found. Please change filter', 'timeout': 5000});
      } else {
        $scope.facets = [];
        messagingService.push({'type': 'info', 'title': 'Search', 'content': 'No document found', 'timeout': 5000});
      }
    } else {
      setPagination();
      // Mark as selected the terms
      _.each($scope.result.facets, function(facet) {
        _.each($scope.result.facetSettings, function(setting) {
          if (setting.name === facet.name) {
            facet.field = setting.terms[0].field;
          }
        });
        _.each(facet.terms, function(term) {
          _.each($scope.terms, function(term2) {
            term.selected = (facet.field === term2.term && term.term === term2.value);
            if (term.selected) {
              return false;
            }
          });
        });
      });
    }
  });

  function getFilters() {
		if ($scope.terms === [] || $scope.terms.length === 0) {
			return null;
		}
		var filters = {};
    $log.log('getFilters - $scope.term: ' + JSON.stringify($scope.terms));
    _.each($scope.terms, function(term) {
      if (filters[term.term] === undefined) {
        filters[term.term] = [];
      }
      filters[term.term].push(term.value);
    });
		return filters;
  }

  function setPagination() {
		var pageSize = $scope.result.pageSize;
		var totalHits = $scope.result.totalHits;
		var firstIndex = $scope.result.firstIndex;
		$scope.totalPages = Math.ceil(totalHits / pageSize);
		$scope.currentPage = 1 + (firstIndex / pageSize);
		$log.log('totalPages: ' + $scope.totalPages + ' - currentPage: ' + $scope.currentPage);
  }

	function getIndexOf(id) {
		for (var i in $scope.result.documents) {
			if ($scope.result.documents[i].id === id) {
				return i;
			}
		}
	}

  $scope.$on('document:updatefacets', function(evt, args) {
    if (args.operation === undefined || args.tag === undefined) {
      return;
    }
    $log.log('document:updatefacets: ' + args.operation + ' - ' + args.tag);
    updateFacets(args.operation, args.tag);
  });

  // $scope.$on('document:remove', function(evt, args) {
  //   if (args === undefined) {
  //     return;
  //   }
  //   var document = args;
  //   $log.log('document:remove - ' + document.id);
  //   var index = getIndexOf(document.id);
  //   _.each(document.tags, function (tag) {
  //     // $rootScope.$broadcast('document:updatefacets', {'operation': 'remove', 'tag': tag});
  //     updateFacets('remove', tag);
  //   });
  //   if (index) {
  //     $scope.result.documents.splice(index, 1);
  //   }
  // });

  // Update facets
  function updateFacets(operation, tag) {
    $log.log('updateFacets: ' + operation + ' - ' + tag);
    var tagFacet = $scope.result.facets.Tags;
    if (tagFacet === undefined) {
      $log.warn('Tags facet not found.');
      return;
    }
    var term = _.find(tagFacet.terms, {'term': tag});
    if ('add' === operation) {
      if (term !== undefined) {
        term.count++;
      } else {
        tagFacet.terms.push({'term': tag, 'count': 1});
      }
    } else if ('remove' === operation) {
      if (term !== undefined) {
        if (term.count > 1) {
          term.count--;
        } else {
          tagFacet.terms.splice(_.indexOf(tagFacet.terms, term), 1);
        }
      }
    }
  }
  
  $scope.setPage = function () {
		$log.log('setPage');
		if ($scope.criteria === undefined ) {
			return;
		}
    find( ($scope.currentPage - 1) * $scope.pageSize, $scope.criteria );
  };

  $scope.$watch('currentPage', $scope.setPage );

  // $scope.$on('document:addtag', function(evt, args) {
		// if (args.id === undefined || args.tag === undefined) {
		// 	return;
		// }
		// $log.log('*** addTag: ' + args.id + ' - ' + args.tag);
		// var id = args.id;
		// var tag = args.tag;
		// documentService.addTag(id, tag, function(doc) {
		// 	var index = getIndexOf(id);
  //     // Update facets
  //     updateFacets('add', tag);
		// 	$scope.documents[index] = doc;
		// });
  // });

  // $scope.$on('document:removetag', function(evt, args) {
		// if (args.id === undefined || args.tag === undefined) {
		// 	return;
		// }
		// $log.log('*** removetag: ' + args.id + ' - ' + args.tag);
		// var id = args.id;
		// var tag = args.tag;
		// documentService.removeTag(id, tag, function(doc) {
		// 	var index = getIndexOf(id);
  //     updateFacets('remove', tag);
		// 	$scope.documents[index] = doc;
		// });
  // });

  $scope.$on('search:applyfacet', function(evt, args) {
    $log.log('applyfacet: ' + JSON.stringify(args));
		if (args.term === undefined || args.value === undefined || args.selected === undefined) {
			return;
		}
		$log.log('*** applyfacet: ' + args.term + ' - ' + args.value + ' - ' + args.selected);
		if (args.selected) {
			$scope.terms.push({'term': args.term, 'value': args.value});
		} else {
			for (var i in $scope.terms) {
				if ($scope.terms[i].term === args.term && $scope.terms[i].value === args.value) {
					$scope.terms.splice(i, 1);
					break;
				}
			}
		}
		find(0, $scope.criteria, true);
  });

}]);
