'use strict';

esDmsSiteApp.controller('LoginCtrl', ['$log', '$scope', 'authenticationService', 'authService', 'sharedService', 'dialog',
  function ($log, $scope, authenticationService, authService, sharedService, dialog) {
    $scope.login = function() {
      $log.log('loginController - login');
      authenticationService.login($scope.username, $scope.password, $scope.rememberMe, function(data) {
        $log.log('data: ' + JSON.stringify(data));
        if (data.status === 'AUTHENTICATED') {
          dialog.close();
        }
      });
    };

    $scope.close = function () {
      dialog.close();
    };
  }]);
