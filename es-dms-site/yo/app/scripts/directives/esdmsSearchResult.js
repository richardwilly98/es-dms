'use strict';

angular.module('esDmsSiteApp')
  .directive('esdmsSearchResult', function ($log) {
    return {
      templateUrl: 'views/documents/search-result.html',
      scope: { criteria: '=' },
      controller: 'SearchCtrl',
      restrict: 'E',
      link: function postLink(scope) {
        scope.$watch('criteria', function() {
          if (scope.criteria !== undefined && scope.criteria !== '') {
            $log.log('esdms-search-result - criteria: ' + scope.criteria);
            scope.search();
          }
        });
      }
    };
  });
