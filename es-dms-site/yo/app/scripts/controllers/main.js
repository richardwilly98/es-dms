'use strict';

esDmsSiteApp.controller('mainController', function ($log, $scope, $location, sharedService, authenticationService) {
  $scope.$location = $location;
  $scope.user = { name: ''};
  $scope.service = sharedService;
  // $scope.$on('handleBroadcast', function() {
  //   $log.log('Receive brodcast message');
  //   //$scope.showLogout = sharedService.message.logout;
		// // $scope.username = sharedService.message.user;
  // });
  
  /*$scope.$watch('service.getUserSettings()',
    function(newValue) {
      $log.log('watch - name: ' + newValue.name);
      if (newValue) {
        $log.log('username ' + newValue.name);
        $scope.user.name = newValue.name;
      }
    });*/

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
});
