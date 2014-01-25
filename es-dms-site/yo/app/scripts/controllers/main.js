'use strict';

esDmsSiteApp.controller('mainController', function ($log, $scope, $location, $modal, sharedService, userService, authenticationService) {
  $scope.$location = $location;
  $scope.user = { name: ''};
  $scope.service = sharedService;

  $scope.$on('event:setCurrentUser', function(event, currentUser) {
    $scope.user = currentUser;
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
  
  $scope.login = function() {
    //authenticationService.logout();
    alert("login");
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
