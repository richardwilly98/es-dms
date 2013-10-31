'use strict';

angular.module('esDmsSiteApp')
  .controller('DocumentsVersionCtrl', ['$log', '$scope', 'documentService', '$modalInstance', 'documentId', '$upload',
    function ($log, $scope, documentService, $modalInstance, documentId, $upload) {
      $scope.document = {};
      $scope.version = {};
      $scope.files = [];
      $scope.percentage = 0;
      $log.log('document details ' + documentId);

      init();

      function init() {
        if (documentId === undefined) {
          $log.log('documentId is undefined!');
          return;
        }
        documentService.metadata(documentId, function(doc) {
          $scope.document = doc;
        });
      };

      $scope.close = function() {
        $modalInstance.close();
      };

      $scope.onFileSelect = function($files) {
        $log.log('onFileSelect');
        $scope.files = $files;
      }

      $scope.upload = function (/*$files*/) {
        $log.log('onFileSelect');
        if ($scope.files.legnth == 0) {
          $scope.uploadMessage = 'No files selected';
          return;
        }
        //$files: an array of files selected, each file has name, size, and type.
        for (var i = 0; i < $scope.files.length; i++) {
          var $file = $scope.files[i];
          $log.log('About to upload: ' + $file);
          var name = $scope.version.name;
          if (name === undefined) {
            name = $file.name;
          }
          $upload.upload({
            url: 'api/documents/' + $scope.document.id + '/versions/_upload', //upload.php script, node.js route, or servlet upload url
            // headers: {'headerKey': 'headerValue'}, withCredential: true,
            data: {name: name},
            file: $file,
            //fileFormDataName: myFile, //(optional) sets 'Content-Desposition' formData name for file
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
