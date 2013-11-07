'use strict';

esDmsSiteApp.controller('NavbarCtrl', ['$log', '$scope', 'sharedService', function ($log, $scope, $sharedService) {
	$scope.showLogout = false;
	$scope.showLogin = true;
	
	$scope.$on('handleBroadcast', function() {
	  $scope.showLogout = $sharedService.getCurrentUser() !== undefined;
	  $scope.showLogin = $sharedService.getCurrentUser() === undefined;
	  $log.log('showLogout on broadcast: ' + $scope.showLogout);
  });
	$scope.tabs = [
		{ 'view': '/home/search', title: 'Search' },
		{ 'view': '/home/upload', title: 'Upload' },
    { 'view': '/home/tags', title: 'Tags' }
    // { 'view': '/index.test-accordion', title: 'Test' },
  ];
	$scope.adminTabs = [
		{ 'view': '/home/admin/users', title: 'Users' },
		{ 'view': '/home/admin/roles', title: 'Roles' }
	];
}]);
