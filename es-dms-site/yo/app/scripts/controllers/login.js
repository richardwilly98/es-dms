'use strict';

esDmsSiteApp.controller('LoginCtrl', ['$log', '$scope', 'authenticationService', 'authService', 'sharedService', '$modalInstance',
  function ($log, $scope, authenticationService, authService, sharedService, $modalInstance) {
    $scope.credential = {};
    $scope.login = function() {
      $log.log('loginController - login - ' + $scope.credential.username);
      authenticationService.login($scope.credential.username, $scope.credential.password, $scope.credential.rememberMe, function(data) {
        $log.log('data: ' + JSON.stringify(data));
        if (data.status === 'AUTHENTICATED') {
          // dialog.close();
          $modalInstance.close();
        }
      });
    };

    $scope.close = function () {
      // dialog.close();
      $modalInstance.dismiss('cancel');
    };
  }]);
