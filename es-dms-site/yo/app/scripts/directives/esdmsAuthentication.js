'use strict';

esDmsSiteApp.directive('esdmsAuthenticationDirective', ['$log', '$dialog', function ($log, $dialog) {
  $log.log('Start esdmsAuthenticationDirective');
  return {
    restrict: 'E',
    scope: { view: '=' },
    link: function($scope) {
      
      $scope.$on('event:auth-loginRequired', function() {
        $log.log('event:auth-loginRequired - Show login');
        var options = {
          backdropFade: true,
          dialogFade: true,
          controller: 'LoginCtrl',
          templateUrl : $scope.view
        };
        var dialog = $dialog.dialog(options);
        dialog.open();
      });
      
      $scope.$on('event:auth-loginConfirmed', function() {
        $log.log('event:auth-loginConfirmed - Hide login');
      });
    }
  };
}]);
