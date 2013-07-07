'use strict';

esDmsSiteApp.controller('mainController', function ($log, $scope, $location, sharedService) {
  $scope.$location = $location;
  $scope.username = '';
  $scope.$on('handleBroadcast', function() {
    $log.log('Receive brodcast message');
    //$scope.showLogout = sharedService.message.logout;
		$scope.username = sharedService.message.user;
  });
});
