'use strict';

esDmsSiteApp.controller('UserCtrl', ['$scope', 'userService', function ($scope, userService) {
  $scope.users = [];
  $scope.totalHits = 0;
  $scope.elapsedTime = 0;
  function init() {
  }

  init();

	function getIndexOf(id) {
		for (var i in $scope.users) {
			if ($scope.users[i].id === id) {
				return i;
			}
		}
	}

	$scope.search = function() {
		userService.search($scope.criteria, function(result) {
			$scope.users = result.items;
			$scope.totalHits = result.totalHits;
			$scope.elapsedTime = result.elapsedTime;
		});
  };
  $scope.edit = function(id) {
    userService.edit(id);
  };
  $scope.remove = function(id) {
		userService.remove(id);
		var index = getIndexOf(id);
		if (index) {
			$scope.users.splice(index, 1);
		}
  };

  $scope.add = function () {
  };
}]);
