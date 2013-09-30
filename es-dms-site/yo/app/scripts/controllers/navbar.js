'use strict';

esDmsSiteApp.controller('NavbarCtrl', ['$scope', function ($scope) {
	$scope.showLogout = true;
	$scope.$on('handleBroadcast', function() {
    //$scope.showLogout = sharedService.message.logout;
  });
	$scope.tabs = [
		{ 'view': '/index.documents-search', title: 'Search' },
		/*{ 'view': '/my-documents-view', title: 'My documents' },*/
		{ 'view': '/index.documents-edit', title: 'Upload' },
    /*{ 'view': '/index.test-accordion', title: 'Test' },
    { 'view': '/view2', title: 'View #2' },
    { 'view': '/view3', title: 'Test View'},
    { 'view': '/view4', title: 'View #4' }*/
  ];
	$scope.adminTabs = [
		{ 'view': '/index.admin-users', title: 'Users' },
		{ 'view': '/index.admin-roles', title: 'Roles' }
	];
}]);
