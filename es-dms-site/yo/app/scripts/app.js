/* exported esDmsSiteApp */
'use strict';

var esDmsSiteApp = angular.module('esDmsSiteApp',
  ['ngCookies', 'ngResource', 'ngSanitize', 'authentication', 'ui.router', 'http-auth-interceptor', 'ui.bootstrap', 'ui.select2']);

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
});

esDmsSiteApp.config(function($provide, $httpProvider, $compileProvider) {
  var elementsList = $();
  var showMessage = function(content, cl, time) {
    $('<div/>')
    .addClass('message')
    .addClass(cl)
    .hide()
    .fadeIn('fast')
    .delay(time)
    .fadeOut('fast', function() { $(this).remove(); })
    .appendTo(elementsList)
    .text(content);
  };
        
  $httpProvider.responseInterceptors.push(function($timeout, $q) {
    return function(promise) {
      return promise.then(function(successResponse) {
        if (successResponse.config.method.toUpperCase() !== 'GET') {
          // showMessage('Success', 'successMessage', 2000);
        }
        return successResponse;
      }, function(errorResponse) {
        switch (errorResponse.status) {
        case 401:
          showMessage('Wrong usename or password', 'errorMessage', 20000);
          break;
        case 403:
          showMessage('You don\'t have the right to do this', 'errorMessage', 20000);
          break;
        case 500:
          showMessage('Server internal error: ' + errorResponse.data, 'errorMessage', 20000);
          break;
        default:
          showMessage('Error ' + errorResponse.status + ': ' + errorResponse.data, 'errorMessage', 20000);
        }
        return $q.reject(errorResponse);
      });
    };
  });

  $compileProvider.directive('appMessages', function() {
    var directiveDefinitionObject = {
      link: function(scope, element) {
        elementsList.push($(element));
      }
    };
    return directiveDefinitionObject;
  });
});

esDmsSiteApp.run(['$log', 'authenticationService', '$cookies', function($log, authenticationService, $cookies) {
  $log.log('*** run ***');
  var token = $cookies.ES_DMS_TICKET;
  if (token !== '') {
    $log.log('initialize - token: ' + token);
    authenticationService.validate(token);
  } else {
    $log.log('No ES_DMS_TICKET cookie found.');
  }
}]);