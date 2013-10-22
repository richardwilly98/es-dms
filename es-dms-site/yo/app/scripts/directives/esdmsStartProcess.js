'use strict';

angular.module('esDmsSiteApp')
  .directive('esdmsStartProcess', ['$log', 'sharedService', 'processService', 'messagingService', function ($log, sharedService, processService, messagingService) {
    return {
      template: '<a class="label" data-ng-show="canStartProcess"><i class="icon-white icon-play"></i>&nbsp;Start Process</a>',
      restrict: 'E',
      link: function postLink(scope, element) {
        scope.canStartProcess = sharedService.isProcessUser();
        element.bind('click', function() {
          // TODO: Maybe move this code in processService...
          $log.log('click start process');
          processService.listProcessDefinitions(function(processDefinitions){
            if (processDefinitions !== undefined && processDefinitions.length > 0) {
              var processDefinition = processDefinitions[0];
              $log.log('Try start process deinition: ' + processDefinition.id);
              processService.startProcessInstance(processDefinition.id, function(instance) {
                $log.log('Process started succesfully: ' + JSON.stringify(instance));
                messagingService.push({ type: 'success', title: 'Process Started', content: 'Process started succesfully ' + instance.id });
                processService.listTaskByProcessInstance(instance.id, function(data){
                  $log.log('listTaskByProcessInstance: ' + JSON.stringify(data));
                  var task = data[0];
                  var userId = sharedService.getCurrentUser().id;
                  processService.assignTask(task.id, userId, function(data2) {
                    $log.log('assignTask return: ' + JSON.stringify(data2));
                  });
                });
              });
            }
          });
        });
      }
    };
  }]);
