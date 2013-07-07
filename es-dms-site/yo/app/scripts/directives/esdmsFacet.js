'use strict';

esDmsSiteApp.directive('esdmsFacet', ['$log', function ($log) {
	$log.log('Start esdmsFacet');
	return {
    restrict: 'E',
    scope: { term: '=', count: '=', selected: '=' },
    template:
      '<div>' +
      '<input type="checkbox" ng-model="selected">' +
      '<a data-ng-click="search(term)">' +
      '{{ term }} - ({{ count }})' +
      '</a>' +
      '</div>',
    link: function ( scope/*, element*/ ) {
			scope.$watch('selected', function() {
				if (scope.selected !== undefined) {
					$log.log('change - ' + scope.selected + ' - term: ' + scope.term);
					scope.$emit('search:applyfacet', {'term': scope.term, 'selected': scope.selected});
				}
			});
    }
	};
}]);
