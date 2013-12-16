'use strict';

angular.module('esDmsSiteApp')
  .directive('esdmsSearchResult', function ($log, $rootScope) {
    return {
      templateUrl: 'views/documents/search-result.html',
      scope: { criteria: '=' },
      controller: 'SearchResultCtrl',
      restrict: 'E',
      link: function postLink(scope) {
        scope.$watch('criteria', function(newValue) {
          if (newValue !== undefined && newValue !== '') {
            $log.log('esdmsSearchResult - model.criteria: ' + newValue);
            $rootScope.$broadcast('search:execute', {'criteria': newValue});
          }
        });
      }
    };
  });
