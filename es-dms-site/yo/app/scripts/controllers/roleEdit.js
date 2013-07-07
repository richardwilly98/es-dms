'use strict';

esDmsSiteApp.controller('RoleEditCtrl', ['$scope', '$rootScope', 'roleService', function ($scope, $rootScope, roleService) {
	$scope.role = {};
	$rootScope.$on('role:edit', function() {
		var editRole = roleService.currentRole();
		if (editRole.id) {
			$scope.role = editRole;
			$scope.newRole = false;
		} else {
			$scope.newRole = true;
			$scope.incomplete = true;
			$scope.role = {};
		}
    $scope.shouldBeOpen = true;
	});
	$scope.save = function() {
		roleService.save($scope.role);
    $scope.shouldBeOpen = false;
	};

  $scope.close = function() {
    $scope.shouldBeOpen = false;
  };

  $scope.opts = {
    backdropFade: true,
    dialogFade:true
  };

}]);
