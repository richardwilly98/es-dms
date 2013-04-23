var simpleApp = angular.module('simpleApp', [
	'ui.bootstrap', 
	'ngResource', 
	'ngCookies', 
	'$strap.directives', 
	'ngUpload', 
	'http-auth-interceptor'
]);

simpleApp.directive('authenticationDirective', function() {
	console.log('Start authenticationDirective');
    return {
      restrict: 'A',
      link: function(scope, elem, attrs) {
        //once Angular is started, remove class:
        //elem.removeClass('waiting-for-angular');
        
        var login = elem.find('#login-holder');
        //var main = elem.find('#content');
        
        login.hide();
        
        scope.$on('event:auth-loginRequired', function() {
        	console.log('event:auth-loginRequired');
          login.slideDown('slow', function() {
            //main.hide();
          });
        });
        scope.$on('event:auth-loginConfirmed', function() {
        	console.log('event:auth-loginConfirmed');
          main.show();
          //login.slideUp();
        });
      }
    }
  });

simpleApp.config(function ($routeProvider) {
    $routeProvider
//		.when('/login', {
//		    controller: 'loginController',
//		    templateUrl: 'views/login.html'
//		})
		.when('/view1', {
		    controller: 'simpleController',
		    templateUrl: 'views/view1.html'
		})
		.when('/view2', {
		    controller: 'simpleController',
		    templateUrl: 'views/view2.html'
		})
		.when('/view3', {
		    controller: 'simpleController',
		    templateUrl: 'views/view3.html'
		})
		.when('/search-view', {
		    controller: 'documentController',
		    templateUrl: 'views/search-view.html'
		})
		.when('/edit-view', {
		    controller: 'documentController',
		    templateUrl: 'views/edit-view.html'
		})
		.otherwise({ redirectTo: '/view1' })
});

simpleApp.factory('userService', function ($resource) {
    return $resource('api/users/:verb/:name', {}, {});
});

simpleApp.factory('documentService', function ($resource) {
    return $resource('api/documents/:verb/:name', {}, {});
});

simpleApp.factory('authenticationService', function ($resource) {
    return $resource('api/auth/:verb/:name', {}, {});
});

function NavBarCtrl($scope) {
    $scope.tabs = [
        { "view": "/view1", title: "View #1" },
        { "view": "/view2", title: "View #2" },
        { "view": "/search-view", title: "Search" },
        { "view": "/edit-view", title: "Edit" },
        { "view": "/view3", title: "Test View" }
    ];
}

