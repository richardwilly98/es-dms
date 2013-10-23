'use strict';

esDmsSiteApp.controller('DocumentPreviewCtrl', ['$timeout','$log', '$scope', 'documentService', '$modalInstance', 'documentId', 'criteria',
  function ($timeout,$log, $scope, documentService, $modalInstance, documentId, criteria) {

  $scope.document = {};

  // TODO: Try to cache preview
  function init() {
    if (documentId === undefined) {
      $log.warn('documentId is not defined.');
      return;
    }
    if (criteria === undefined) {
      $log.warn('documentId is not defined.');
      return;
    }
    $scope.document.id = documentId;
    documentService.preview(documentId, criteria, function(response) {
      $scope.document.preview = response;
    });
  };

  $scope.close = function() {
    $modalInstance.close();
  }

  init();

}]);
