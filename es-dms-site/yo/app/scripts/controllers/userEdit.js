'use strict';

esDmsSiteApp.controller('UserEditCtrl', ['$scope', '$rootScope', '$http', 'userService', function ($scope, $rootScope, $http, userService) {
	$scope.user = null;
	$scope.newUser = false;
	$scope.uid = '';
	$scope.user = {};
	$scope.pw1 = '';
	$scope.pw2 = '';
	$scope.pwError = false;
	$scope.incomplete = false;

	$rootScope.$on('user:edit', function() {
		var editUser = userService.currentUser();
		if (editUser.id) {
			$scope.user = editUser;
			$scope.newUser = false;
		} else {
			$scope.newUser = true;
			$scope.incomplete = true;
			$scope.user = {};
			$scope.pw1 = '';
			$scope.pw2 = '';
		}
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
