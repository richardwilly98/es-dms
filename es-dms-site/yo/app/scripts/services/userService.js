'use strict';

esDmsSiteApp.service('userService', ['$log', '$rootScope', '$resource', '$http', function userService($log, $rootScope, $resource, $http) {
	var resource = $resource('api/users/:verb/:name', {}, {});
	var userResource = $resource('api/users/:id/:action/:parameter' , {id:'@id'}, {
		metadata: {method:'GET', params: {action: 'metadata'}},
		update: {method:'PUT', params: {}}
	});
	
	var users = [];
	var editedUser = null;
	
	return {
		search: function(criteria, callback) {
			$http.get('api/users/search/' + criteria).success(function (data) {
				users = data.items;
				callback(data);
			});
		},
		edit: function(id) {
			$log.log('edit user: ' + id);
			if (id === 'new') {
				editedUser = null;
			} else {
				editedUser = id;
			}
			$rootScope.$broadcast('user:edit');
		},
		remove: function(id) {
			$log.log('delete document: ' + id);
			var user = new userResource.get({'id': id});
			user.$delete({'id': id});
		},
		currentUser: function() {
			if (editedUser) {
				for (var i in users) {
					if (users[i].id === editedUser) {
						return users[i];
					}
				}
			} else {
				return {};
			}
		},
		save: function(user) {
			$log.log('save user: ' + user);
			resource.save(user);
			if (!editedUser) {
				users.push(user);
			}
		}
	};
}]);
