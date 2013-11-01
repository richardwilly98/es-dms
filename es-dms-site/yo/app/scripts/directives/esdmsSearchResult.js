'use strict';

angular.module('esDmsSiteApp')
  .directive('esdmsSearchResult', function ($log) {
    return {
      templateUrl: 'views/documents/search-result.html',
      scope: { criteria: '=' },
      controller: 'DocumentCtrl',
      restrict: 'E',
      link: function postLink(scope) {
        scope.$watch('criteria', function() {
          $log.log('criteria: ' + scope.criteria);
          if (scope.criteria !== undefined && scope.criteria !== '') {
            scope.search();
          }
        });
      }
    };
  });
