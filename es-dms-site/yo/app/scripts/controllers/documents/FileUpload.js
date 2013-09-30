'use strict';

esDmsSiteApp.controller('DocumentsFileUploadCtrl', 
  ['$scope', '$rootScope', 'uploadService', 'userService', function ($scope, $rootScope, uploadService, userService) {

  $scope.shouldBeOpen = false;
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
    $scope.shouldBeOpen = false;
  };
  
  $scope.open = function() {
    // TODO: Hack to check if user if authenticated before to display the form
    // userService.get('admin', function (data) {
      $scope.shouldBeOpen = true;
    // });
  };

  $scope.opts = {
    backdropFade: true,
    dialogFade:true
  };

}]);
