'use strict';

esDmsSiteApp.service('systemService', ['$log', function ($log) {
		
	//get version from system
	var systemVersion = '1.0';
		
	this.version = function() {
		$log.log('ES-DMS system Version:' + systemVersion);
		return systemVersion;
	};
}]);
