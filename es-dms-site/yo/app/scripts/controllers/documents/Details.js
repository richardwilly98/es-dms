'use strict';

angular.module('esDmsSiteApp')
  .controller('DocumentsDetailsCtrl', ['$log', '$scope', '$rootScope', 'documentService', function ($log, $scope, $rootScope, documentService) {
  $scope.shouldBeOpen = false;
  $scope.document = {};
  $scope.auditEntries = {};
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
  
  $scope.loadAudit = function() {
    $log.log('loadAudit: ' + $scope.document.id);
    documentService.audit($scope.document.id, function(auditEntries) {
      //$scope.auditEntries = auditEntries.items;
      $scope.auditEntries = _.map(auditEntries.items, function(auditEntry) {
        auditEntry.date = new Date(auditEntry.date);
        return auditEntry;
      });
    });
  };
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
