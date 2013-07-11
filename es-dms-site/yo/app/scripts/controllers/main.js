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
  $scope.$watch('service.getUserSettings()',
    function(newValue) {
      $log.log('watch - name: ' + newValue.name);
      if (newValue) {
        $log.log('username ' + newValue.name);
        $scope.user.name = newValue.name;
      }
    });

  $scope.logout = function() {
    authenticationService.logout();
  };
});
