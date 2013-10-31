'use strict';

esDmsSiteApp.controller('ModalDocumentsFileUploadCtrl', ['$scope', '$modal',
  function ($scope, $modal) {
    $scope.open = function() {
      $modal.open({
        templateUrl: 'views/documents/file-upload-new.html',
        controller: 'DocumentsFileUploadCtrl'
      });
    };
}]);

esDmsSiteApp.controller('DocumentsFileUploadCtrl', 
  ['$log', '$scope', '$rootScope', '$modalInstance', '$upload', 'userService',
  function ($log, $scope, $rootScope, $modalInstance, $upload, userService) {

  $scope.files = [];
  $scope.percentage = 0;
  $scope.uploadMessage = 'No files selected';

  $scope.close = function() {
    $modalInstance.close();
  };
  
  $scope.onFileSelect = function($files) {
    $log.log('onFileSelect');
    $scope.files = $files;
  }

  $scope.upload = function (/*$files*/) {
    $log.log('onFileSelect');
    if ($scope.files.length == 0) {
      $scope.uploadMessage = 'No files selected';
      return;
    }
    //$files: an array of files selected, each file has name, size, and type.
    for (var i = 0; i < $scope.files.length; i++) {
      var $file = $scope.files[i];
      $log.log('About to upload: ' + $file);
      $upload.upload({
        url: 'api/documents/_upload', 
        file: $file,
        progress: function(evt) {
          var percentage = parseInt(100.0 * evt.loaded / evt.total);
          $log.log('percent: ' + percentage);
          $scope.percentage = percentage;
          $scope.$apply();
        }
      }).success(function(data, status, headers, config) {
        // file is uploaded successfully
        $log.log('upload success - ' + headers('location'));
        $scope.percentage = 0;
        $scope.uploadMessage = $scope.files.length + ' file(s) have been uploaded';
        $scope.files = [];

      })
      //.error(...).then(...); 
    }
  }
}]);
