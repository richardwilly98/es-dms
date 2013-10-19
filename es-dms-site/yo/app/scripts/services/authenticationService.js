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
		},
		validate: function(token) {
			esdmsAuthenticationService.validate(token, function(response) {
				if (response !== undefined) {
					userService.get(response.id, function(user){
						$log.log('user -> ' + JSON.stringify(user));
						sharedService.setCurrentUser(user);
					});
				}
			});
		}
	};
}]);
