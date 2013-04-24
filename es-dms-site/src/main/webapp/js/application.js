var simpleApp = angular.module('simpleApp', [
	'ui.bootstrap', 
	'ngResource', 
	'ngCookies', 
	'$strap.directives', 
	'ngUpload', 
	'http-auth-interceptor'
]);

simpleApp.directive('authenticationDirective', function($dialog) {
	console.log('Start authenticationDirective');
    return {
      restrict: 'A',
      link: function(scope, elem, attrs) {
        
        var login = elem.find('#login-holder');
        var main = elem.find('#content');
        
        login.hide();
        
        scope.$on('event:auth-loginRequired', function() {
        	login.show()
        	login.slideDown('slow', function() {
        		main.hide();
        	});
        });
        scope.$on('event:auth-loginConfirmed', function() {
        	main.show();
        	login.slideUp();
        	login.hide();
        });
      }
    }
  });

simpleApp.config(function ($routeProvider) {
    $routeProvider
		.when('/login', {
		    controller: 'loginController',
		    templateUrl: 'views/login.html'
		})
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

simpleApp.factory('sharedService', function($rootScope) {
    var sharedService = {};
    
    sharedService.message = '';

    sharedService.prepForBroadcast = function(msg) {
        this.message = msg;
        this.broadcastItem();
    };

    sharedService.broadcastItem = function() {
        $rootScope.$broadcast('handleBroadcast');
    };

    return sharedService;
});

simpleApp.factory('userService', function ($resource) {
    return $resource('api/users/:verb/:name', {}, {});
});

simpleApp.factory('documentService', function ($resource) {
    return $resource('api/documents/:verb/:name', {}, {});
});

simpleApp.factory('authenticationService', function ($http) {
	return {
		login: function(username, password, callback) {
			var payload = {username: username, password: password};
			var config = {
					headers: {'Content-Type':'application/json; charset=UTF-8'}
			};
			$http.post('api/auth/login', payload, config).success(callback);
		}
	};
});

function NavBarCtrl($scope, sharedService) {
	$scope.showLogout = false;
	$scope.$on('handleBroadcast', function() {
        $scope.showLogout = sharedService.message.logout;
    }); 
	
	$scope.tabs = [
        { "view": "/view1", title: "View #1" },
        { "view": "/view2", title: "View #2" },
        { "view": "/search-view", title: "Search" },
        { "view": "/edit-view", title: "Edit" },
        { "view": "/view3", title: "Test View" }
    ];
}

