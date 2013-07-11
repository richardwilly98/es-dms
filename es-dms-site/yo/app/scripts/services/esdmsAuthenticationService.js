'use strict';

esDmsSiteApp.service('esdmsAuthenticationService', ['$http', function ($http) {
  return {
    login: function(username, password, rememberMe, callback) {
      var payload = {username: username, password: password, rememberMe: rememberMe};
      var config = {
        headers: {'Content-Type':'application/json; charset=UTF-8'}
      };
      $http.post('api/auth/login', payload, config).success(callback);
    },
    logout: function() {
      $http.post('api/auth/logout');
    }
  };
}]);
