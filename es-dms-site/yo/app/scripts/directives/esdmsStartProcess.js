'use strict';

angular.module('esDmsSiteApp')
  .directive('esdmsStartProcess', ['$log', 'sharedService', function ($log, sharedService) {
    return {
      template: '<a class="label" data-ng-show="canStartProcess"><i class="icon-white icon-play"></i>&nbsp;Start Process</a>',
      restrict: 'E',
      link: function postLink(scope, element) {
        scope.canStartProcess = sharedService.getUserSettings().isProcessUser;
        element.bind('click', function() {
          // TODO: should be provided by directive attributes.
          $log.log('click start process');
        });
      }
    };
  }]);
