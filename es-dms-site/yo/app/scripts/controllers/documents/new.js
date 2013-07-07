'use strict';

esDmsSiteApp.controller('DocumentsNewCtrl', ['$log', '$scope', function ($log, $scope) {
	$scope.showAlert = false;
  $scope.alert = {};
	$scope.newDocument = {};
  $scope.uploadComplete = function (content, completed) {
		$log.log('uploadComplete ' + content + ' - ' + completed);
    if (completed && content.length > 0) {
			var response = JSON.parse(content);
			$log.log(response);
      // Clear form (reason for using the 'ng-model' directive on the
			// input elements)
      $scope.newDocument.name = '';
      $scope.newDocument.date = '';
      // $scope.newDocument.id = response.id;
      $scope.alert = { type: 'success', msg: 'New document created #' + response.id};
      $scope.showAlert = true;
      // Look for way to clear the input[type=file] element
    }
  };
  $scope.closeAlert = function() {
    $scope.showAlert = false;
    $scope.alert = {};
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
