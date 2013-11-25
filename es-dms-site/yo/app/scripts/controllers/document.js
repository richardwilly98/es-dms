'use strict';

esDmsSiteApp.controller('DocumentCtrl', ['$log', '$scope', '$rootScope', '$modal', 'documentService',
  function ($log, $scope, $rootScope, $modal, documentService) {

  $scope.$on('document:addtag', function(evt, args) {
		if (args.id === undefined || args.tag === undefined) {
			return;
		}
		$log.log('*** addTag: ' + args.id + ' - ' + args.tag);
		var id = args.id;
		var tag = args.tag;
		documentService.addTag(id, tag, function() {
      // Update facets
      $rootScope.$broadcast('document:updatefacets', {'operation': 'add', 'tag': tag});
		});
  });

  $scope.$on('document:removetag', function(evt, args) {
		if (args.id === undefined || args.tag === undefined) {
			return;
		}
		$log.log('*** removetag: ' + args.id + ' - ' + args.tag);
		var id = args.id;
		var tag = args.tag;
		documentService.removeTag(id, tag, function() {
      // updateFacets('remove', tag);
      $rootScope.$broadcast('document:updatefacets', {'operation': 'remove', 'tag': tag});
		});
  });

  $scope.showDetails = function() {
    documentService.showDetails($scope.document.id);
    $modal.open({
      templateUrl: 'views/documents/details.html',
      controller: 'DocumentsDetailsCtrl',
      resolve: {
        documentId: function() {
          return $scope.document.id;
        }
      }
    });
  };

  $scope.checkout = function() {
		// documentService.checkout($scope.document.id).$promise.then(function(){document.attributes.status = 'L';});
    documentService.checkout($scope.document.id);
		$scope.document.attributes.status = 'L';
  };

  $scope.checkin = function() {
		documentService.checkin($scope.document.id);
		$scope.document.attributes.status = 'A';
  };

  $scope.addVersion = function() {
    $modal.open({
      templateUrl: 'views/documents/add-version.html',
      controller: 'DocumentsVersionCtrl',
      resolve: {
        documentId: function() {
          return $scope.document.id;
        }
      }
    });
  };

  $scope.remove = function() {
		documentService.remove($scope.document.id, function(data, status) {
      $log.log('remove -> ' + data + ' - ' + status);
      $rootScope.$broadcast('document:remove', $scope.document);
    });
  };

}]);
