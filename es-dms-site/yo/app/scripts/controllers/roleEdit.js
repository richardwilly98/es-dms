'use strict';

esDmsSiteApp.controller('RoleEditCtrl', ['$log', '$scope', '$rootScope', '$modalInstance', 'roleService', 'roleId',
  function ($log, $scope, $rootScope, $modalInstance, roleService, roleId) {
	$scope.role = {};
  $scope.roleTypes = [];
	
  function init() {
    $log.log('init: ' + roleId);
    loadRoleTypes();
		roleService.currentRole(function(role) {
        if (role.id) {
          $scope.role = role;
          $scope.newRole = false;
        } else {
          $scope.newRole = true;
          $scope.incomplete = true;
          $scope.role = role;
        }
      });
  }

  function loadRoleTypes() {
    $log.log('loadRoleTypes');
    roleService.roleTypes(function(result) {
      $log.log('loadRoleTypes -> ' + JSON.stringify(result));
      $scope.roleTypes = result;
    });
  }

	$scope.save = function() {
		roleService.save($scope.role, $scope.newRole);
    $modalInstance.close();
	};

  $scope.close = function() {
    $modalInstance.close();
  };

  init();

}]);
