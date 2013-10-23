'use strict';

angular.module('esDmsSiteApp')
  .directive('esdmsPreview', ['$log', '$modal', function ($log, $modal) {
    return {
      template: '<a class="label"><i class="icon-white icon-picture"></i>&nbsp;Preview</a>',
      restrict: 'E',
      scope: { id: '=', criteria: '='},
      link: function (scope, element) {
        element.bind('click', function() {
          $log.log('click preview for ' + scope.id + ' with criteria ' + scope.criteria);
          $modal.open({
            templateUrl: 'views/documents/preview.html',
            controller: 'DocumentPreviewCtrl',
            resolve: {
              documentId: function() {
                return scope.id;
              },
              criteria: function() {
                return scope.criteria;
              }
            }
          });
        });
      }
    };
  }]);
