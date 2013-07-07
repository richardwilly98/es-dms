'use strict';

esDmsSiteApp.controller('DocumentsUploadCtrl', ['$log', '$scope', '$http', 'fileUpload', function ($log, $scope, $http, fileUpload) {
	var url = 'api/documents/upload';
	$scope.shouldBeOpen = false;
	$scope.loadingFiles = false;
	$scope.options = {
		url: url
	};
	$http.get(url)
		.then(
      function (response) {
        $scope.loadingFiles = false;
        $scope.queue = response.data.files;
      },
      function () {
        $scope.loadingFiles = false;
      }
  );
	fileUpload.fileuploaddone = function(e, data) {
		$log.log('fileuploaddone - ' + e + ' - ' + data);
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

esDmsSiteApp.controller('fileDestroyController', ['$scope', '$http', function ($scope, $http) {
	var file = $scope.file, state;
	if (file.url) {
		file.$state = function () {
			return state;
		};
		file.$destroy = function () {
			state = 'pending';
			return $http({
				url: file.delete_url,
				method: file.delete_type
			}).then(
					function () {
						state = 'resolved';
						$scope.clear(file);
					},
					function () {
						state = 'rejected';
					}
			);
		};
	} else if (!file.$cancel) {
		file.$cancel = function () {
			$scope.clear(file);
		};
	}
}]);

