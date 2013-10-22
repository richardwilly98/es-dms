'use strict';

esDmsSiteApp.service('uploadService', ['$rootScope', function uploadService($rootScope) {
  var _files = [];
  var _fileCount = 0;
  return {
    add: function (file) {
      _files.push(file);
      $rootScope.$broadcast('uploadService:fileAdded', file.files[0].name);
    },
    clear: function () {
      _files = [];
    },
    files: function () {
      var fileNames = [];
      $.each(_files, function (index, file) {
        fileNames.push(file.files[0].name);
      });
      return fileNames;
    },
    upload: function () {
      _fileCount = _files.length;
      $.each(_files, function (index, file) {
        file.submit();
      });
      this.clear();
    },
    setProgress: function (percentage) {
      _fileCount--;
      $rootScope.$broadcast('uploadService:uploadProgress', percentage);
      if (_fileCount === 0) {
        $rootScope.$broadcast('uploadService:uploadCompleted');
      }
    }
  };
}]);
