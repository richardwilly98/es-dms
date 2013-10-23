'use strict';

esDmsSiteApp.controller('DocumentPreviewCtrl', ['$log', '$scope', 'documentService', '$modalInstance', 'documentId', 'criteria',
  function ($log, $scope, documentService, $modalInstance, documentId, criteria) {

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
      if (response !== null) {
        $scope.document.preview = response;
      } else {
        $scope.document.preview = 'No preview available for document ' + $scope.document.id;
      }
    });
  };

  $scope.close = function() {
    $modalInstance.close();
  }

  init();

}]);
