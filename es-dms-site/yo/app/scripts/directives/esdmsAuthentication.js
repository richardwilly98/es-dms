'use strict';

esDmsSiteApp.directive('esdmsAuthenticationDirective', ['$log', '$modal', function ($log, $modal) {
  $log.log('Start esdmsAuthenticationDirective');
  return {
    restrict: 'E',
    scope: { view: '=' },
    link: function($scope) {
      
      $scope.$on('event:auth-loginRequired', function() {
        $log.log('event:auth-loginRequired - Show login');
        $modal.open({
          templateUrl : $scope.view,
          controller: 'LoginCtrl'
        });
      });
      
      $scope.$on('event:auth-loginConfirmed', function() {
        $log.log('event:auth-loginConfirmed - Hide login');
      });
    }
  };
}]);
