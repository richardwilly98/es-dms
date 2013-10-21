/* exported sharedService */
'use strict';

esDmsSiteApp.service('sharedService', ['$log', '$rootScope', function ($log, $rootScope) {
  var settings = {
    user : {
      pageSize: 12,
      isProcessUser: true
    },
    search : {
      criteria: null,
      facet: null,
      facets: [],
      terms: [],
      documents: [],
      totalHits: 0,
      totalPages: 0,
      elapsedTime: 0,
      maxPages: 10,
      currentPage: 1
    }
  };
  
  var currentUser = {};
  var message = '';
  function $hasRole(id) {
    if (currentUser !== null) {
      var role = _.find(currentUser.roles, {'id' : id});
      return (role !== undefined);
    } else {
      return false;
    }
  }
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
      return $hasRole(id);
    },
    // hasRole: function(id) {
    //   if (currentUser !== null) {
    //     var role = _.find(currentUser.roles, {'id' : id});
    //     return (role !== undefined);
    //   } else {
    //     return false;
    //   }
    // },
    hasPermission: function(id) {
      if (currentUser !== null) {
        $log.log('hasPermission: ' + id + ' in ' + currentUser.id);
        var permission;
        _.each(currentUser.roles, function(role) {
          permission = _.find(role.permissions, {'id' : id});
          if (permission !== undefined) {
            return false;
          }
        });
        return (permission !== undefined);
      } else {
        return false;
      }
    },
    isProcessUser: function () {
      if (currentUser !== null) {
        return $hasRole('process-admin') || $hasRole('process-user');
      } else {
        return false;
      }
    }
  };
}]);
