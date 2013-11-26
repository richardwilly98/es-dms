'use strict';

angular.module('esDmsSiteApp')
  .directive('esdmsSearchResultHeader', function ($log, messagingService) {
    return {
      // template: '<div class="alert alert-success" data-ng-show="result.totalHits" type="success">' +
      //   'About {{ result.totalHits }} results - search took {{ result.elapsedTime }} ms - items per page : {{ result.pageSize }}' +
      //   '</div>',
      scope: { },
      // controller: 'SearchResultCtrl',
      restrict: 'E',
      link: function postLink(scope) {
        scope.$on('facetedSearch:result', function(evt, args) {
          $log.log('esdmsSearchResultHeader - facetedSearch:result');
          scope.result = args;
          if (scope.result.totalHits !== 0) {
            messagingService.push({'type': 'info', 'title': 'Search', 'content': 'About ' + scope.result.totalHits + ' results - search took ' + scope.result.elapsedTime + ' ms', 'timeout': 5000});
          }
        });
      }
    };
  });
