'use strict';

esDmsSiteApp.controller('NavbarCtrl', ['$scope', function ($scope) {
	$scope.showLogout = true;
	$scope.$on('handleBroadcast', function() {
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
