'use strict';

esDmsSiteApp.service('searchService', ['$http', function searchService($http) {
	return {
		facetedSearch: function(first, pageSize, criteria, facet, filters, callback) {
			var payload = {facet: facet, filters: filters};
			var config = {
        headers: {'Content-Type':'application/json; charset=UTF-8'}
			};
			$http.post('api/search/_facet_search/' + criteria + '?fi=' + first + '&ps=' + pageSize, payload, config).success(callback);
		}
	};
}]);
