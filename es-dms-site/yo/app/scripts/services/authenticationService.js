'use strict';

esDmsSiteApp.service('authenticationService', ['esdmsAuthenticationService', 'authService', 'sharedService', function (esdmsAuthenticationService, authService, sharedService) {
	return {
		login: function(username, password, rememberMe, callback) {
			esdmsAuthenticationService.login(username, password, rememberMe, function(data){
				if (data.status === 'AUTHENTICATED') {
					authService.loginConfirmed();
					sharedService.prepForBroadcast({logout: true});
	        sharedService.updateUserSettings('name', username);
				}
        callback(data);
			});
		},
		logout: function() {
			esdmsAuthenticationService.logout();
			sharedService.updateUserSettings('name', null);
		}
	};
}]);
