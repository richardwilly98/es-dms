'use strict';

esDmsSiteApp.directive('esdmsFacet', ['$log', function ($log) {
	$log.log('Start esdmsFacet');
	return {
    restrict: 'E',
    scope: { term: '=' },
    template:
      '<div>' +
      '<a data-ng-click="toggle()" class="label">{{ term.term }}</a>' +
      ' ' + 
      '<span class="badge badge-info">{{ term.count }}</span>' +
      '</div>',
    link: function ( scope, element ) {
      scope.toggle = function() {
        scope.selected = !scope.selected;
        $log.log('select term: ' + scope.term.term);
        var label = element.find('.label');
        label.toggleClass('label-info');
        scope.$emit('search:applyfacet', {'term': scope.term.term, 'selected': scope.selected});
      };
    }
	};
}]);
