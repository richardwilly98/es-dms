'use strict';

angular.module('esDmsSiteApp')
  .service('processService', ['$log', '$resource', function ProcessService($log, $resource) {
    var resource = $resource('api/process/:action', {}, {
      listProcessDefinitions: {method:'GET', params: {action: 'process-definitions'}, isArray: true},
      startProcessInstance: {method:'POST', params: {action: 'process-instances'}},
      attach: {method:'POST', params: {action: 'process-instances'}}
    });
    var processDefinitions = [];
    return {
      listProcessDefinitions: function(callback) {
        $log.log('Get process definitions');
        if (processDefinitions.length === 0) {
          var definitions = resource.listProcessDefinitions(function(){
            $log.log('get definitions: ' + JSON.stringify(definitions));
            processDefinitions = definitions;
            callback(definitions);
          });
        }
      },
      startProcessInstance: function(id) {
        $log.log('Start process instance from process definition: ' + id);
      }
    };
  }]);
