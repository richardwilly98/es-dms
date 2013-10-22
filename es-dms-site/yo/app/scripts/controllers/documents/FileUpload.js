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
  ['$log', '$scope', '$rootScope', '$modalInstance', 'uploadService', 'userService',
  function ($log, $scope, $rootScope, $modalInstance, uploadService, userService) {

  $scope.files = [];
  $scope.percentage = 0;
  $scope.uploadMessage = 'No files selected';

  $scope.upload = function () {
    uploadService.upload();
  };

  $rootScope.$on('uploadService:fileAdded', function (e, call) {
    $scope.files.push(call);
    $scope.uploadMessage = '';
    $scope.$apply();
  });

  $rootScope.$on('uploadService:uploadProgress', function (e, call) {
    $log.log('uploadProgress: ' + call)
    $scope.percentage = call;
    $scope.$apply();
  });

  $rootScope.$on('uploadService:uploadCompleted', function() {
    $scope.uploadMessage = $scope.files.length + ' file(s) have been uploaded';
    $scope.files = [];
  });
  
  $scope.close = function() {
    $modalInstance.close();
  };
  
}]);
