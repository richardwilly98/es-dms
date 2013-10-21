'use strict';

esDmsSiteApp.controller('mainController', function ($log, $scope, $location, $modal, sharedService, userService, authenticationService) {
  $scope.$location = $location;
  $scope.user = { name: ''};
  $scope.service = sharedService;

  $scope.$watch('service.getCurrentUser()',
    function(newValue) {
      if (newValue !== null) {
        $log.log('watch - currentUser: ' + newValue.email);
        $scope.user = newValue;
      } else {
        $scope.user = { name: ''};
      }
    });

  $scope.hasRole = function(id) {
    return sharedService.hasRole(id);
  };

  $scope.hasPermission = function(id) {
    return sharedService.hasPermission(id);
  };

  $scope.logout = function() {
    authenticationService.logout();
  };

  $scope.showUserDetails = function() {
    var userId = sharedService.getCurrentUser().id;
    if (userId !== undefined) {
      userService.edit(userId);
      $modal.open({
          templateUrl: 'views/users/edit-user.html',
          controller: 'UserEditCtrl',
          resolve: {
            userId: function() {
              return userId;
            }
          }
        });
    }
  };
});
