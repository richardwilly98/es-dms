'use strict';

esDmsSiteApp.controller('ModalSearchOptionsCtrl', ['$scope', '$modal',
  function ($scope, $modal) {
    $scope.open = function() {
      $modal.open({
        templateUrl: 'views/search/options.html',
        controller: 'SearchOptionsCtrl'
      });
    };
}]);

esDmsSiteApp.controller('SearchOptionsCtrl', ['$log', '$scope', '$modalInstance', 'sharedService',
  function ($log, $scope, $modalInstance, sharedService) {
    $scope.pageSizeList = [12, 24, 48, 96];
    $scope.options = {pageSize: sharedService.getSettings().user.pageSize};

    $scope.save = function() {
      sharedService.updateUserSettings('pageSize', $scope.options.pageSize);
      $modalInstance.close();
    };

    $scope.close = function() {
      $modalInstance.close();
    };

}]);
