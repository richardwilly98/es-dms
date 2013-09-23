'use strict';

angular.module('esDmsSiteApp')
  .controller('DocumentsDetailsCtrl', ['$log', '$scope', '$rootScope', 'documentService', function ($log, $scope, $rootScope, documentService) {
  $scope.shouldBeOpen = false;
  $scope.document = {};
  $scope.isAvailable = false;

  $rootScope.$on('document:showDetails', function() {
    var current = documentService.current();
    if (current) {
      $scope.document.id = current;
      documentService.metadata(current, function(doc) {
        $scope.document = doc;
      });
    } else {
      $scope.document = {};
    }
    $scope.shouldBeOpen = true;
  });
  
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

  }]);
