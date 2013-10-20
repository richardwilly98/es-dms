'use strict';

esDmsSiteApp.controller('ModalDocumentsFileUploadCtrl', ['$scope', '$modal',
  function ($scope, $modal) {
    $scope.open = function() {
      $modal.open({
        templateUrl: 'views/documents/file-upload.html',
        controller: 'DocumentsFileUploadCtrl'
      });
    };
}]);

esDmsSiteApp.controller('DocumentsFileUploadCtrl', 
  ['$scope', '$rootScope', '$modalInstance', 'uploadService', 'userService', function ($scope, $rootScope, $modalInstance, uploadService, userService) {

  $scope.files = [];
  $scope.percentage = 0;

  $scope.upload = function () {
    uploadService.upload();
    $scope.files = [];
  };

  $rootScope.$on('fileAdded', function (e, call) {
    $scope.files.push(call);
    $scope.$apply();
  });

  $rootScope.$on('uploadProgress', function (e, call) {
    $scope.percentage = call;
    $scope.$apply();
  });

  $scope.close = function() {
    $modalInstance.close();
  };
  
}]);
