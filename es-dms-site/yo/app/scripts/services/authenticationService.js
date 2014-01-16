'use strict';

esDmsSiteApp.service('authenticationService', ['$log', '$http', 'esdmsAuthenticationService', 'authService', 'sharedService', 'userService',
	function ($log, $http, esdmsAuthenticationService, authService, sharedService, userService) {
	return {
		login: function(username, password, rememberMe, callback) {
			esdmsAuthenticationService.login(username, password, rememberMe, function(data){
				if (data.status === 'AUTHENTICATED') {
					$http.defaults.headers.common['X-ESDMSTICKET'] = data.token;
					authService.loginConfirmed();
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
			delete $http.defaults.headers.common['X-ESDMSTICKET'];
		},
		validate: function(token, callback) {
			esdmsAuthenticationService.validate(token, function(response) {
				if (response !== undefined) {
					userService.get(response.id, function(user) {
						sharedService.setCurrentUser(user);
						callback();
					});
				}
			});
		}
	};
}]);
