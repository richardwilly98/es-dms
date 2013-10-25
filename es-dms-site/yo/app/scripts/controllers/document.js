'use strict';

esDmsSiteApp.controller('DocumentCtrl', ['$log', '$scope', '$modal', 'documentService', 'searchService', 'sharedService', 'messagingService',
  function ($log, $scope, $modal, documentService, searchService, sharedService, messagingService) {

  $scope.documents = [];
  $scope.facets = [];
  $scope.totalHits = 0;
  $scope.elapsedTime = 0;
  $scope.maxPages = 10;
  $scope.totalPages = 0;
  $scope.currentPage = 1;
  $scope.newtag = {};
  $scope.terms = [];
  $scope.service = sharedService;
  $scope.pageSize = $scope.service.getSettings().user.pageSize;
  
  // Define facet settings. It is recommended to keep at least 'Tags' facet
  $scope.facetSettings = [
    {'name' : 'Tags', terms: [{'field': 'tags', 'size': 10}]},
    {'name' : 'Language', terms: [{'field': 'versions.file.language', 'size': 10}]},
    {'name' : 'Author', terms: [{'field': 'attributes.author', 'size': 10}]}
  ];

  function init() {
  }

  init();

  $scope.$watch('service.getUserSettings()',
    function(newValue) {
      $log.log('watch - pageSize: ' + newValue.pageSize);
      if (newValue && $scope.pageSize !== newValue.pageSize) {
        $scope.pageSize = newValue.pageSize;
        $scope.search();
      }
    });

  $scope.search = function(/*term*/) {
		$log.log('search');
		if ($scope.criteria === undefined || $scope.criteria === '' || $scope.criteria === '*') {
      messagingService.push({'type': 'info', 'title': 'Search', 'content': 'Empty or wildcard not allowed', 'timeout': 2000});
      $scope.totalHits = 0;
      $scope.totalPages = 0;
			$scope.documents = [];
      $scope.terms = [];
      $scope.facets = [];
      searchService.criteria(null);
		} else {
      $scope.documents = [];
      $scope.terms = [];
      $scope.facets = [];
      searchService.criteria($scope.criteria);
			find(0, $scope.criteria, true);
		}
  };

  function find(first, criteria, updatePagination) {
		var filters = getFilters();
    $log.log('About to execute facetedSearch with filter: ' + JSON.stringify(filters));
		searchService.facetedSearch(first, $scope.pageSize, criteria, $scope.facetSettings, filters, function(result) {
			if (updatePagination) {
				setPagination(result);
			}

      $scope.documents = result.items;
      $scope.totalHits = result.totalHits;
      if ($scope.totalHits === 0) {
        if (filters !== {}) {
          messagingService.push({'type': 'warning', 'title': 'Search', 'content': 'No document found. Please change filter', 'timeout': 2000});
        } else {
          $scope.facets = [];
          messagingService.push({'type': 'info', 'title': 'Search', 'content': 'No document found', 'timeout': 2000});
        }
      } else {
        $scope.elapsedTime = result.elapsedTime;
        $scope.facets = result.facets;

        // Mark as selected the terms
        _.each($scope.facets, function(facet) {
          _.each($scope.facetSettings, function(setting) {
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
  }

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

  function setPagination(result) {
		var pageSize = result.pageSize;
		var totalHits = result.totalHits;
		var firstIndex = result.firstIndex;
		$scope.totalPages = Math.ceil(totalHits / pageSize);
		$scope.currentPage = 1 + (firstIndex / pageSize);
		$log.log('totalPages: ' + $scope.totalPages + ' - currentPage: ' + $scope.currentPage);
  }

	function getDocument(id) {
		var documents = $scope.documents;
		for (var i in documents) {
			if (documents[i].id === id) {
				return documents[i];
			}
		}
	}

	function getIndexOf(id) {
		for (var i in $scope.documents) {
			if ($scope.documents[i].id === id) {
				return i;
			}
		}
	}

  // Update facets
  function updateFacets(operation, tag) {
    var tagFacet = $scope.facets.Tags;
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

  $scope.$on('document:addtag', function(evt, args) {
		if (args.id === undefined || args.tag === undefined) {
			return;
		}
		$log.log('*** addTag: ' + args.id + ' - ' + args.tag);
		var id = args.id;
		var tag = args.tag;
		documentService.addTag(id, tag, function(doc) {
			var index = getIndexOf(id);
      // Update facets
      updateFacets('add', tag);
			$scope.documents[index] = doc;
		});
  });

  $scope.$on('document:removetag', function(evt, args) {
		if (args.id === undefined || args.tag === undefined) {
			return;
		}
		$log.log('*** removetag: ' + args.id + ' - ' + args.tag);
		var id = args.id;
		var tag = args.tag;
		documentService.removeTag(id, tag, function(doc) {
			var index = getIndexOf(id);
      updateFacets('remove', tag);
			$scope.documents[index] = doc;
		});
  });

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

  $scope.showDetails = function(id) {
    documentService.showDetails(id);
    $scope.documentId = id;
    $modal.open({
      templateUrl: 'views/documents/details.html',
      controller: 'DocumentsDetailsCtrl',
      resolve: {
        documentId: function() {
          return $scope.documentId;
        }
      }
    });
  };

  $scope.checkout = function(id) {
		documentService.checkout(id);
		var document = getDocument(id);
		if (document) {
			document.attributes.status = 'L';
		}
  };

  $scope.checkin = function(id) {
		documentService.checkin(id);
		var document = getDocument(id);
		if (document) {
			document.attributes.status = 'A';
		}
  };

  $scope.remove = function(id) {
		documentService.remove(id, function(data, status) {
      $log.log('remove -> ' + data + ' - ' + status);
      var document = getDocument(id);
      _.each(document.tags, function (tag) {
        updateFacets('remove', tag);
      });
      var index = getIndexOf(id);
      if (index) {
        $scope.documents.splice(index, 1);
      }
    });
  };

}]);
