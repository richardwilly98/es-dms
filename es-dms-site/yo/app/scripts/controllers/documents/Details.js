'use strict';

angular.module('esDmsSiteApp')
  .controller('DocumentsDetailsCtrl', ['$log', '$scope', '$rootScope', '$state', 'documentService', 'searchService', 
    function ($log, $scope, $rootScope, $state, documentService, searchService) {
  $scope.shouldBeOpen = false;
  $scope.document = {};
  $scope.auditEntries = {};
  $scope.moreLikeThis = {};
  $scope.isAvailable = false;
  $scope.tags = null;

  $rootScope.$on('document:showDetails', function() {
    var current = documentService.current();
    if (current) {
      $scope.document.id = current;
      documentService.metadata(current, function(doc) {
        $scope.document = doc;
        if (doc.tags) {
          $scope.tags = _(doc.tags).toString();
        }
        $log.log('$scope.tags: ' + $scope.tags);
      });
    } else {
      $scope.document = {};
    }
    $scope.shouldBeOpen = true;
  });
  
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
