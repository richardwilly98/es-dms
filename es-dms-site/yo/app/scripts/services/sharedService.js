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
  return {
    setCurrentUser: function (cu) {
      currentUser = cu;
    },
    getCurrentUser: function () {
      return currentUser;
    },
    prepForBroadcast: function(msg) {
      message = msg;
      $rootScope.$broadcast('handleBroadcast');
    },
    updateUserSettings: function(name, val) {
      var _user = angular.copy(settings.user);
      _user[name] = val;
      settings.user = _user;
    },
    getUserSettings: function() {
      return settings.user;
    },
    getSettings: function() {
      return settings;
    },
    hasRole: function(id) {
      if (currentUser !== null) {
        var role = _.find(currentUser.roles, {'id' : id});
        return (role !== undefined);
      } else {
        return false;
      }
    }
  };
});
