'use strict';

angular.module('esDmsSiteApp')
  .service('messagingService', ['toaster', function messagingService(toaster) {
    return {
      push: function(message) {
        var type = 'info';
        if (message.type !== undefined) {
          type = message.type;
        }
        var title = message.title;
        var body = message.content;
        var timeout;
        if (message.timeout !== undefined) {
          timeout = message.timeout;
        }
        toaster.pop(type, title, body, timeout);
      }
    };
  }]);
