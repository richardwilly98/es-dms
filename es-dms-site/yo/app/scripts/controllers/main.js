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
    var userId = sharedService.getCurrentUser().id;
    if (!userId) {
      $modal.open({
          templateUrl: 'views/authentication/login.html',
          controller: 'LoginCtrl',
          resolve: {
            userId: function() {
              return userId;
            }
          }
        });
    }
  };
  
  $scope.showAboutDetails = function() {
      $modal.open({
          templateUrl: 'views/about/credits.html',
          controller: 'CreditsCtrl',
        });
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
