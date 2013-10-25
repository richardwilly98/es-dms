'use strict';

esDmsSiteApp.directive('esdmsFacet', ['$log', function ($log) {
  $log.log('Start esdmsFacet');
  return {
    restrict: 'E',
    scope: { facet: '=' },
    template:
      '<div>' +
      '<h5>{{ facet.name }}</h5>' +
      '<div data-ng-repeat="term in facet.terms">' +
      '<a data-ng-click="toggle(term)" data-ng-class="toggleClass(term)">{{ term.term }}</a>' +
      ' ' +
      '<span class="badge badge-info">{{ term.count }}</span>' +
      '</div>' +
      '</div>',
    link: function ( scope ) {
      scope.toggle = function(term) {
        $log.log('click'  + JSON.stringify(term));
        term.selected = !term.selected;
        scope.$emit('search:applyfacet', {'term': scope.facet.field, 'value': term.term, 'selected': term.selected});
      };
      scope.toggleClass = function(term) {
        if (term.selected === undefined || !term.selected) {
          return 'label';
        } else {
          return 'label label-info';
        }
      };
    }
  };
}]);
