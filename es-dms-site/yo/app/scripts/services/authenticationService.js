'use strict';

esDmsSiteApp.service('authenticationService', ['$log', '$http', 'esdmsAuthenticationService', 'authService', 'sharedService', 'userService',
	function ($log, $http, esdmsAuthenticationService, authService, sharedService, userService) {
	return {
		login: function(username, password, rememberMe, callback) {
			esdmsAuthenticationService.login(username, password, rememberMe, function(data){
				if (data.status === 'AUTHENTICATED') {
					authService.loginConfirmed();
					sharedService.prepForBroadcast({logout: true});
	        sharedService.updateUserSettings('name', username);
	        userService.get(username, function(user) {
						sharedService.setCurrentUser(user);
	        });
				}
        callback(data);
			});
		},
		logout: function() {
			esdmsAuthenticationService.logout();
			sharedService.setCurrentUser(null);
		},
		validate: function(token) {
			esdmsAuthenticationService.validate(token, function(response) {
				if (response !== undefined) {
					userService.get(response.id, function(user) {
						sharedService.setCurrentUser(user);
					});
				}
			});
		}
	};
}]);
