'use strict';

esDmsSiteApp.service('authenticationService', ['esdmsAuthenticationService', 'authService', 'sharedService', 'userService',
	function (esdmsAuthenticationService, authService, sharedService, userService) {
	return {
		login: function(username, password, rememberMe, callback) {
			esdmsAuthenticationService.login(username, password, rememberMe, function(data){
				if (data.status === 'AUTHENTICATED') {
					authService.loginConfirmed();
					sharedService.prepForBroadcast({logout: true});
	        sharedService.updateUserSettings('name', username);
	        userService.get(username, function(data) {
						sharedService.setCurrentUser(data);
	        });
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
