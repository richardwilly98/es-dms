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
      var label = element.find('.label');
      if (scope.term.selected) {
        label.toggleClass('label-info');
      }
      scope.toggle = function() {
        scope.term.selected = !scope.term.selected;
        scope.$emit('search:applyfacet', {'term': scope.term.term, 'selected': scope.term.selected});
      };
    }
	};
}]);
