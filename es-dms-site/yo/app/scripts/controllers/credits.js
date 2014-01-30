'use strict';

esDmsSiteApp.controller('CreditsCtrl', ['$log', '$scope', '$modalInstance', 'systemService',
	function ($log, $scope, $modalInstance, systemService ) {

		function init() {
			$log.log('Iintializing credits dialog');
			
			if (!$scope.system) $scope.system = new Object();
			$scope.system.version = systemService.version();
		}
	
		$scope.close = function() {
			$log.log('Closing credits dialog');
		    $modalInstance.close();
		 };
		 
		 init();
}]);