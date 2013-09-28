'use strict';

esDmsSiteApp.controller('UserEditCtrl', ['$log', '$scope', '$rootScope', '$http', 'userService', 'roleService',
	function ($log, $scope, $rootScope, $http, userService, roleService) {
	$scope.user = null;
	$scope.newUser = false;
	$scope.uid = '';
	$scope.user = {};
	$scope.pw1 = '';
	$scope.pw2 = '';
	$scope.pwError = false;
	$scope.incomplete = false;
	$scope.selectedRoles = [];
	$scope.roles = [];

	$rootScope.$on('user:edit', function() {
		$log.log('on user:edit');
		var editUser = userService.currentUser();
		if (editUser.id) {
			$scope.user = editUser;
			$log.log('user.roles: ' + JSON.stringify($scope.user.roles));
			//$scope.selectedRoles = $scope.user.roles;
			$scope.newUser = false;
		} else {
			$scope.newUser = true;
			$scope.incomplete = true;
			$scope.user = {roles: []};
			$scope.pw1 = '';
			$scope.pw2 = '';
		}
		roleService.search('*', function(result) {
			$scope.roles = result.items;
			if (!$scope.newUser) {
				// TODO: Must be optimized.
				// TODO: Check the reason orderBy filter does not work. Items not selected.
				_.each($scope.roles, function(role) {
					_.find($scope.user.roles, function(item) {
						if (item != null && role.id == item.id) {
							$scope.selectedRoles.push(role);
						}
					})
				});
				$log.log('$scope.selectedRoles: ' + JSON.stringify($scope.selectedRoles));
			}
			$log.log('roles: ' + JSON.stringify($scope.roles));
		});
    $scope.shouldBeOpen = true;
	});
	
  $scope.close = function() {
    $scope.shouldBeOpen = false;
  };

  $scope.opts = {
    backdropFade: true,
    dialogFade:true
  };

	$scope.save = function() {
		if ($scope.newUser) {
			$scope.user.password = $scope.pw1;
		}
		$log.log('About to save user: ' + JSON.stringify($scope.user));
		$scope.user.roles = $scope.selectedRoles;
		userService.save($scope.user);
    $scope.shouldBeOpen = false;
	};
	
	$scope.$watch('pw1', function() {
		$scope.pwError = $scope.pw1 !== $scope.pw2;
		$scope.incompleteTest();
	});
	
	$scope.$watch('pw2', function() {
		$scope.pwError = $scope.pw1 !== $scope.pw2;
		$scope.incompleteTest();
	});

	$scope.$watch('username', function() {
		$scope.incompleteTest();
	});

	$scope.incompleteTest = function() {
		if ($scope.newUser) {
			$scope.incomplete = !$scope.user.name.length || !$scope.pw1.length || !$scope.pw2.length;
		} else {
			$scope.incomplete = false;
		}
	};
}]);
