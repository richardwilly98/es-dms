'use strict';

esDmsSiteApp.controller('SearchOptionsCtrl', function ($log, $scope, sharedService) {
	$scope.shouldBeOpen = false;
  $scope.pageSizeList = [12, 24, 48, 96];
  $scope.pageSize = sharedService.getSettings().user.pageSize;

  $scope.save = function() {
    sharedService.updateUserSettings('pageSize', $scope.pageSize);
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
});
