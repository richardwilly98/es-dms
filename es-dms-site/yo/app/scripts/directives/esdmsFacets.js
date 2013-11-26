'use strict';

esDmsSiteApp.directive('esdmsFacets', ['$log', function ($log) {
  $log.log('Start esdmsFacets');
  return {
    restrict: 'E',
    scope: {},
    template:
      '<div class="well sidebar-nav" data-ng-show="result.facets">' +
        '<ul class="nav nav-list" data-ng-repeat="facet in result.facets">' +
          '<li>' +
            '<esdms-facet facet="facet" />' +
          '</li>' +
        '</ul>' +
      '</div>',
    link: function ( scope ) {
      scope.$on('facetedSearch:result', function(evt, args) {
        $log.log('esdmsFacets - facetedSearch:result');
        scope.result = args;
        if (scope.result.totalHits !== 0) {
          // Mark as selected the terms
          _.each(scope.result.facets, function(facet) {
            _.each(scope.result.facetSettings, function(setting) {
              if (setting.name === facet.name) {
                facet.field = setting.terms[0].field;
              }
            });
            // _.each(facet.terms, function(term) {
            //   _.each($scope.terms, function(term2) {
            //     term.selected = (facet.field === term2.term && term.term === term2.value);
            //     if (term.selected) {
            //       return false;
            //     }
            //   });
            // });
          });
        }
      });
    }
  };
}]);
