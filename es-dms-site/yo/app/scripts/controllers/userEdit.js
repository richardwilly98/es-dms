'use strict';

esDmsSiteApp.controller('UserEditCtrl', ['$log', '$scope', '$modalInstance', '$rootScope', '$http', 'userService', 'roleService', 'userId',
	function ($log, $scope, $modalInstance, $rootScope, $http, userService, roleService, userId) {
	$scope.newUser = false;
	$scope.user = {};
	$scope.password = {
		'pw1': '',
		'pw2': ''
	};
	$scope.pwError = false;
	$scope.incomplete = false;
	$scope.selected = { roles: [] };
	$scope.roles = [];

	$log.log('Edit user: ' + userId);

	function init() {
		userService.currentUser(function(user) {
			if (user.id !== undefined) {
				$scope.user = user;
				$log.log('user.roles: ' + JSON.stringify($scope.user.roles));
				$scope.newUser = false;
			} else {
				$scope.newUser = true;
				$scope.incomplete = true;
				$scope.user = user;
			}
			roleService.search('*', function(result) {
				$scope.roles = result.items;
				if (!$scope.newUser) {
					// TODO: Must be optimized.
					// TODO: Check the reason orderBy filter does not work. Items not selected.
					_.each($scope.roles, function(role) {
						_.find($scope.user.roles, function(item) {
							if (item !== null && role.id === item.id) {
								$scope.selected.roles.push(role);
							}
						});
					});
					$log.log('$scope.selected.roles: ' + JSON.stringify($scope.selected.roles));
				}
				$log.log('roles: ' + JSON.stringify($scope.roles));
			});
		});
	}
	
  $scope.close = function() {
    $modalInstance.close();
  };

	$scope.save = function() {
		if ($scope.newUser) {
			$scope.user.id = $scope.user.login;
			$scope.user.password = $scope.password.pw1;
		}
		$scope.user.roles = $scope.selected.roles;
		userService.save($scope.user, $scope.newUser);
    $modalInstance.close();
	};
	
	$scope.$watch('password.pw1', function() {
		$scope.pwError = $scope.password.pw1 !== $scope.password.pw2;
		$scope.incompleteTest();
	});
	
	$scope.$watch('password.pw2', function() {
		$scope.pwError = $scope.password.pw1 !== $scope.password.pw2;
		$scope.incompleteTest();
	});

	$scope.$watch('user.name', function() {
		$scope.incompleteTest();
	});

	$scope.incompleteTest = function() {
		if ($scope.newUser && $scope.user.name !== undefined) {
			$scope.incomplete = !$scope.user.name.length || !$scope.password.pw1.length || !$scope.password.pw2.length;
		} else {
			$scope.incomplete = false;
		}
	};

	init();
}]);
