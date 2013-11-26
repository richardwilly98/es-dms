'use strict';

esDmsSiteApp.service('searchService', ['$log', '$rootScope', '$http', function searchService($log, $rootScope, $http) {
  var currentCriteria = null;
	return {
    criteria : function(criteria) {
      currentCriteria = criteria;
    },
		facetedSearch: function(first, pageSize, criteria, facets, filters) {
			var payload = {facets: facets, filters: filters};
			var config = {
        headers: {'Content-Type':'application/json; charset=UTF-8'}
			};
      $http.post('api/search/_facet_search/' + criteria + '?fi=' + first + '&ps=' + pageSize, payload, config)
        .success(function(data) {
          var result = {
            firstIndex: data.firstIndex,
            pageSize: data.pageSize,
            documents: data.items,
            facetSettings: facets,
            totalHits: data.totalHits
          };

          if (result.totalHits !== 0) {
            result.elapsedTime = data.elapsedTime;
            result.facets = data.facets;
          }
          $rootScope.$broadcast('facetedSearch:result', result);
        })
        .error(function(data, status) {
          $log.log('Faceted search failed ' + status + ' - ' + data);
        });
		},
    moreLikeThis: function(first, pageSize, criteria, minTermFrequency, maxQueryTerms, callback) {
      if (criteria === null) {
        criteria = currentCriteria;
      }
      var config = {
        headers: {'Content-Type':'application/json; charset=UTF-8'}
      };
      $http.get('api/search/_more_like_this/' + criteria + '?fi=' + first + '&ps=' + pageSize + '&mt=' + minTermFrequency + '&mi=' + maxQueryTerms, config)
        .success(callback);
    },
    suggestTags: function(criteria, size, callback) {
      if (size === undefined) {
        size = 10;
      }

      var payload = {};
      var config = {
        headers: {'Content-Type':'application/json; charset=UTF-8'}
      };
      $http.post('api/search/tags/_suggest/' + criteria + '?si=' + size, payload, config).success(callback);
    },
	};
}]);
