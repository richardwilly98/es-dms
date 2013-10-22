'use strict';

angular.module('esDmsSiteApp')
  .directive('esdmsStartProcess', ['$log', 'sharedService', 'processService', 'messagingService',
    function ($log, sharedService, processService, messagingService) {
    return {
      template: '<a class="label" data-ng-show="canStartProcess"><i class="icon-white icon-play"></i>&nbsp;Start Process</a>',
      restrict: 'E',
      scope: { id: '=' },
      link: function postLink(scope, element) {
        scope.canStartProcess = sharedService.isProcessUser();
        element.bind('click', function() {
          // TODO: Maybe move this code in processService...
          processService.listProcessDefinitions(function(processDefinitions){
            if (processDefinitions !== undefined && processDefinitions.length > 0) {
              var processDefinition = processDefinitions[0];
              processService.startProcessInstance(processDefinition.id, function(instance) {
                $log.info('Process started succesfully: ' + JSON.stringify(instance));
                processService.listTaskByProcessInstance(instance.id, function(data){
                  var task = data[0];
                  var userId = sharedService.getCurrentUser().id;
                  processService.assignTask(task.id, userId, function() {
                    processService.attach(instance.id, scope.id, function() {
                      messagingService.push({ type: 'success', title: 'Process Started', content: 'Process started succesfully ' + instance.id });
                    });
                  });
                });
              });
            }
          });
        });
      }
    };
  }]);
