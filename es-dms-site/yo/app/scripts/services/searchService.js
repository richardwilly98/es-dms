'use strict';

esDmsSiteApp.service('searchService', ['$http', function searchService($http) {
  var currentCriteria = null;
	return {
    criteria : function(criteria) {
      currentCriteria = criteria;
    },
		facetedSearch: function(first, pageSize, criteria, facets, filters, callback) {
			var payload = {facets: facets, filters: filters};
			var config = {
        headers: {'Content-Type':'application/json; charset=UTF-8'}
			};
			$http.post('api/search/_facet_search/' + criteria + '?fi=' + first + '&ps=' + pageSize, payload, config).success(callback);
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
    suggestTags: function(criteria, callback) {
      var size = 10;
      var payload = {};
      var config = {
        headers: {'Content-Type':'application/json; charset=UTF-8'}
      };
      $http.post('api/search/tags/_suggest/' + criteria + '?size=' + size, payload, config).success(callback);
    },
	};
}]);
