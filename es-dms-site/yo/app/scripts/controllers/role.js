'use strict';

esDmsSiteApp.controller('RoleCtrl', ['$scope', 'roleService', function ($scope, roleService) {
  $scope.roles = [];
  $scope.totalHits = 0;
  $scope.elapsedTime = 0;
  
  function init() {
  }

  init();

  $scope.search = function() {
    roleService.search($scope.criteria, function(result) {
      $scope.roles = result.items;
      $scope.totalHits = result.totalHits;
      $scope.elapsedTime = result.elapsedTime;
    });
  };
  
  $scope.edit = function(id) {
    roleService.edit(id);
  };
  
  $scope.add = function () {
  };
}]);
