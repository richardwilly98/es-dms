'use strict';

esDmsSiteApp.controller('SearchOptionsCtrl', ['$scope', function ($scope) {
	$scope.shouldBeOpen = false;

  $scope.save = function() {
    $scope.shouldBeOpen = false;
  };

  $scope.close = function() {
    $scope.shouldBeOpen = false;
  };

  $scope.open = function() {
    $scope.shouldBeOpen = true;
  };

  $scope.opts = {
    backdropFade: true,
    dialogFade:true
  };
}]);
