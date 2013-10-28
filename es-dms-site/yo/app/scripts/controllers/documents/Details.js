'use strict';

angular.module('esDmsSiteApp')
  .controller('DocumentsDetailsCtrl', ['$log', '$scope', '$state', '$filter', 'documentService', 'searchService', '$modalInstance', 'documentId',
    function ($log, $scope, $state, $filter, documentService, searchService, $modalInstance, documentId) {
  $scope.document = {};
  $scope.auditEntries = {};
  $scope.moreLikeThis = {};
  $scope.isAvailable = false;
  $scope.tags = null;

  $log.log('document details ' + documentId);
  init();

  function init() {
    if (documentId === undefined) {
      $log.log('documentId is undefined!');
      return;
    }
    documentService.metadata(documentId, function(doc) {
      $scope.document = doc;
      if (doc.tags) {
        $scope.tags = _(doc.tags).toString();
      }
      $log.log('$scope.tags: ' + $scope.tags);
    });
  };
  
  $scope.loadAudit = function() {
    $log.log('loadAudit: ' + $scope.document.id);
    $log.log('$state.params: ' + JSON.stringify($state.params));
    documentService.audit($scope.document.id, function(auditEntries) {
      $scope.auditEntries = _.map(auditEntries.items, function(auditEntry) {
        auditEntry.date = new Date(auditEntry.date);
        return auditEntry;
      });
    });
  };
  $scope.moreLikeThis = function() {
    $log.log('Start moreLikeThis');
    searchService.moreLikeThis(0, 5, null, 1, 1, function(result) {
      $log.log('moreLikeThis result: ' + JSON.stringify(result));
      $scope.moreLikeThis = result.items;
    });
    $log.log('End moreLikeThis');
  };
  $scope.close = function() {
    $modalInstance.close();
  };
  // TODO: name should not be hardcoded. We should get the mapping definition and check field type.
  $scope.formatValue = function(name, value) {
    if (name === 'creation' || name === 'modified') {
      return $filter('date')(value, 'yyyy-MM-dd HH:mm:ss Z');
    } else {
      return value;
    }
  };

  }]);