'use strict';

esDmsSiteApp.controller('RoleEditCtrl', ['$scope', '$rootScope', 'roleService', function ($scope, $rootScope, roleService) {
	$scope.role = {};
  $scope.roleTypes = [];
	$rootScope.$on('role:edit', function() {
    loadRoleTypes();
		var editRole = roleService.currentRole(function(role){
        if (role.id) {
          $scope.role = role;
          $scope.newRole = false;
        } else {
          $scope.newRole = true;
          $scope.incomplete = true;
          // $scope.role = {};
          $scope.role = role;
        }
        $scope.shouldBeOpen = true;
      });
    });

  function loadRoleTypes() {
    roleService.roleTypes(function(result) {
      $scope.roleTypes = result;
    });
  }
 //    var editRole = roleService.currentRole();
	// 	if (editRole.id) {
	// 		$scope.role = editRole;
	// 		$scope.newRole = false;
	// 	} else {
	// 		$scope.newRole = true;
	// 		$scope.incomplete = true;
	// 		$scope.role = {};
	// 	}
 //    $scope.shouldBeOpen = true;
	// });
	$scope.save = function() {
		roleService.save($scope.role, $scope.newRole);
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
