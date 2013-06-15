var simpleApp = angular.module('simpleApp', [
	'ui.bootstrap', 
	'ngResource', 
	'ngCookies', 
	'$strap.directives', 
	'ngUpload', 
	'http-auth-interceptor',
	'blueimp.fileupload'
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
		.when('/view4', {
		    controller: 'simpleController',
		    templateUrl: 'views/view4.html'
		})
		.when('/search-view', {
		    controller: 'documentController',
		    templateUrl: 'views/documents/search-view.html'
		})
		.when('/edit-view', {
		    controller: 'documentController',
		    templateUrl: 'views/documents/edit-view.html'
		})
		.when('/admin/users', {
		    controller: 'adminController',
		    templateUrl: 'views/user-list.html'
		})
		.when('/admin/roles', {
		    controller: 'adminController',
		    templateUrl: 'views/role-list.html'
		})
		.otherwise({ redirectTo: '/search-view' })
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

simpleApp.factory('userService', function ($rootScope, $resource) {
	var resource = $resource('api/users/:verb/:name', {}, {});
	var users = [];
	var editedUser = null;
	
	return {
		find: function(criteria) {
			users = resource.query({ verb: 'find', name: criteria });
			return users;
		},
		edit: function(id) {
			console.log('edit user: ' + id);
			if (id == 'new') {
				editedUser = null;
			} else {
				editedUser = id;
			}
			$rootScope.$broadcast('user:edit');
		},
		currentUser: function() {
			if (editedUser) {
				for (i in users) {
					if (users[i].id == editedUser) {
						return users[i];
					}
				}
			} else {
				return {};
			}
		},
		save: function(user) {
			console.log('save user: ' + user);
			resource.save(user);
			if (!editedUser) {
				users.push(user);
			}
		}
	};
});

simpleApp.factory('roleService', function ($rootScope, $resource) {
	var resource = $resource('api/roles/:verb/:name', {}, {});
	var roles = [];
	var editedRole = null;
	
	return {
		find: function(criteria) {
			roles = resource.query({ verb: 'find', name: criteria });
			return roles;
		},
		edit: function(id) {
			console.log('edit role: ' + id);
			if (id == 'new') {
				editedRole = null;
			} else {
				editedRole = id;
			}
			$rootScope.$broadcast('role:edit');
		},
		currentRole: function() {
			if (editedRole) {
				for (i in roles) {
					if (roles[i].id == editedRole) {
						return roles[i];
					}
				}
			} else {
				return {};
			}
		},
		save: function(role) {
			console.log('save role: ' + role);
			resource.save(role);
			if (!editedRole) {
				roles.push(role);
			}
		}
	};
});

simpleApp.factory('documentService', function ($resource) {
	var resource = $resource('api/documents/:verb/:name', {}, {
	});
	var documentResource = $resource('api/documents/:id/:action/:parameter' , {}, {
		checkout: {method:'POST', params: {action: 'checkout'}},
		checkin: {method:'POST', params: {action: 'checkin'}},
		preview: {method:'GET', params: {action: 'preview'}}
	})
	var documents = {};
	return {
		find: function(criteria) {
			documents = resource.query({ verb: 'search', name: criteria });
			return documents;
		},
		edit: function(id) {
			console.log('edit document: ' + id);
		},
		checkout: function(id) {
			console.log('checkout document: ' + id);
			var doc = new documentResource.get({'id': id})
			doc.$checkout({'id': id});
		},
		checkin: function(id) {
			console.log('checkin document: ' + id);
			var doc = new documentResource.get({'id': id})
			doc.$checkin({'id': id});
		},
		delete: function(id) {
			console.log('delete document: ' + id);
			var doc = new documentResource.get({'id': id})
			doc.$delete({'id': id});
		},
		preview: function(id, criteria, callback) {
			console.log('preview document: ' + id + ' - criteria: ' + criteria);
			var response = documentResource.preview({'id': id, 'cr': criteria/*, 'fs': 100*/}, function () {
				callback(response.content);
			});
		},
		getDocument: function(id) {
			for (i in documents) {
				if (documents[i].id == id) {
					return documents[i];
				}
			}
		},
		getIndexOf: function(id) {
			for (i in documents) {
				if (documents[i].id == id) {
					return i;
				}
			}
		}
	};
    
});

simpleApp.factory('authenticationService', function ($http) {
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
});
