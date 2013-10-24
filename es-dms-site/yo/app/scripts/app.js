/* exported esDmsSiteApp */
'use strict';

var esDmsSiteApp = angular.module('esDmsSiteApp',
  ['ngCookies', 'ngResource', 'ngSanitize', 'authentication', 'ui.router', 'http-auth-interceptor', 'ui.bootstrap', 'ui.select2', 'toaster']);

esDmsSiteApp.config(function (/*$routeProvider,*/ $stateProvider, $locationProvider) {

  $stateProvider
    .state('index', {
      url: '',
      templateUrl: 'views/main.html'
    })
    .state('index.documents-search', {
      url: '/index.documents-search',
      templateUrl: 'views/documents/search.html'
    })
    .state('index.documents-edit', {
      url: '/index.documents-edit',
      templateUrl: 'views/documents/edit.html'
    })
    .state('index.admin-users', {
      url: '/index.admin-users',
      templateUrl: 'views/users/search-users.html'
    })
    .state('index.admin-roles', {
      url: '/index.admin-roles',
      templateUrl: 'views/roles/search-roles.html'
    })
    .state('index.test-accordion', {
      url: '/index.test-accordion',
      templateUrl: 'views/test/accordion.html'
    })
    /*.state('documents', {
      url: '/documents'
    })
    .state('documents.details', {
      url: '/details'
    })
    .state('documents.details.item', {
      url: '/:item',
      templateUrl: 'views/documents/details.html',
      controller: 'DocumentsDetailsCtrl'
    })*/
    ;

  $locationProvider.html5Mode(false);
  // $locationProvider.hashPrefix('!');
});

esDmsSiteApp.config(['$httpProvider', function($httpProvider) {
        
  $httpProvider.responseInterceptors.push(function($timeout, $q, messagingService) {
    return function(promise) {
      return promise.then(function(successResponse) {
        if (successResponse.config.method.toUpperCase() !== 'GET') {
          // messagingService.push({'type': 'info', 'title': 'Success', 'content': 'successMessage', 'timeout': 1000});
        }
        return successResponse;
      }, function(errorResponse) {
        switch (errorResponse.status) {
        case 401:
          messagingService.push({'type': 'error', 'title': 'Login failed', 'content': 'Wrong usename or password', 'timeout': 5000});
          break;
        case 403:
          messagingService.push({'type': 'error', 'title': 'Access Denied', 'content': 'You don\'t have the right to do this', 'timeout': 5000});
          break;
        case 500:
          messagingService.push({'type': 'error', 'title': 'Server Error', 'content': 'Server internal error:' + errorResponse.data, 'timeout': 5000});
          break;
        default:
          messagingService.push({'type': 'error', 'title': 'Error', 'content': 'Error ' + errorResponse.status + ': ' + errorResponse.data, 'timeout': 5000});
        }
        return $q.reject(errorResponse);
      });
    };
  });

}]);

esDmsSiteApp.run(['$log', 'authenticationService', 'sharedService', '$cookies', function($log, authenticationService, sharedService, $cookies) {
  $log.log('*** run ***');
  var token = $cookies.ES_DMS_TICKET;
  if (token !== '') {
    $log.log('initialize - token: ' + token);
    authenticationService.validate(token, function () {
      sharedService.loadSystemSettings();
    });
  } else {
    $log.log('No ES_DMS_TICKET cookie found.');
  }
}]);