'use strict';

esDmsSiteApp.controller('NavbarCtrl', ['$log', '$scope', function ($log, $scope) {
  $scope.showLogout = false;
  
  $scope.$on('event:setCurrentUser', function(currentUser) {
    $scope.showLogout = currentUser !== null;
    $log.log('showLogout on broadcast: ' + $scope.showLogout);
  });

  $scope.tabs = [
    { 'view': 'home/search', title: 'Search' },
    { 'view': 'home/upload', title: 'Upload' },
    { 'view': 'home/tags', title: 'Tags' }
    // { 'view': '/index.test-accordion', title: 'Test' },
  ];

  $scope.adminTabs = [
    { 'view': 'home/admin/users', title: 'Users' },
    { 'view': 'home/admin/roles', title: 'Roles' }
  ];
}]);
