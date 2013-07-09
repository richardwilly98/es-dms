/* exported sharedService */
'use strict';

esDmsSiteApp.service('sharedService', function ($log, $rootScope) {
  var settings = {
    user : {
      pageSize: 12
    }
  };
  var currentUser = {};
  var message = '';
  /*return {
    getCurrentUser: function () {
      return currentUser;
    },
    prepForBroadcast: function(msg) {
      this.message = msg;
      $rootScope.$broadcast('handleBroadcast');
    },
    updateSettings: function(name, val) {
      $log.log('updateSettings: ' + name + ' - ' + val);
      settings.user[name] = val;
    },
    getSettings: function() {
      return settings;
    }
  };*/
  this.getCurrentUser = function () {
    return currentUser;
  };
  this.prepForBroadcast = function(msg) {
    message = msg;
    $rootScope.$broadcast('handleBroadcast');
  };
  this.updateUserSettings = function(name, val) {
    $log.log('updateUserSettings: ' + name + ' - ' + val);
    settings.user[name] = val;
  };
  this.getSettings = function() {
    return settings;
  };

});
