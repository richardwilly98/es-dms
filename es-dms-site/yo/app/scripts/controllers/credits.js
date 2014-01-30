'use strict';

esDmsSiteApp.controller('CreditsCtrl', ['$log', '$scope', '$modalInstance',
	function ($log, $scope, $modalInstance) {

		function init() {
			$log.log('Iintializing credits dialog');
		}
	
		$scope.close = function() {
			$log.log('Closing credits dialog');
		    $modalInstance.close();
		 };
		 
		 init();
}]);