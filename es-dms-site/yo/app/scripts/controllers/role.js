'use strict';

esDmsSiteApp.controller('RoleCtrl', ['$log', '$scope', 'roleService', function ($log, $scope, roleService) {
  $scope.roles = [];
  // $scope.roleTypes = [];
  $scope.totalHits = 0;
  $scope.elapsedTime = 0;
  
  function init() {
    // roleService.roleTypes(function(result) {
    //   $scope.roleTypes = result;
    // });
  }

  init();

  function search() {
    roleService.search($scope.criteria, function(result) {
      $scope.roles = result.items;
      $scope.totalHits = result.totalHits;
      $scope.elapsedTime = result.elapsedTime;
    });
  }

  $scope.search = function(){
    search();
  };
  /*function() {
    roleService.search($scope.criteria, function(result) {
      $scope.roles = result.items;
      $scope.totalHits = result.totalHits;
      $scope.elapsedTime = result.elapsedTime;
    });
  };*/
  
  $scope.edit = function(id) {
    roleService.edit(id);
  };
  
  $scope.add = function () {
  };

  $scope.$on('role:updated', function(evt, args) {
    $log.log('role:updated ' + args.id);
    search();
  });

}]);
