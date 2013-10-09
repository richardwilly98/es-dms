'use strict';

esDmsSiteApp.service('userService', ['$log', '$rootScope', '$resource', '$http', function userService($log, $rootScope, $resource, $http) {
	// var resource = $resource('api/users/:verb/:name', {}, {});
	var UserResource = $resource('api/users/:id/:action/:parameter' , { /*id:'@id'*/ }, {
		metadata: {method:'GET', params: {action: 'metadata'}},
		create: {method: 'POST', params: {}},
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
			var user = new UserResource.get({'id': id});
			user.$delete({'id': id});
		},
		currentUser: function(callback) {
			if (editedUser) {
				var user = new UserResource.get({'id': editedUser}, function(){
					callback(user);
				});
			} else {
				return callback(new UserResource());
			}
			// if (editedUser) {
			// 	for (var i in users) {
			// 		if (users[i].id === editedUser) {
			// 			return users[i];
			// 		}
			// 	}
			// } else {
			// 	return {};
			// }
		},
		save: function(user, isNew) {
			$log.log('save user: ' + JSON.stringify(user));
			if (isNew) {
				user.$create({}, function() {
					$rootScope.$broadcast('user:updated', {id: user.id});
				});
			} else {
				user.$update({id: user.id}, function() {
					$rootScope.$broadcast('user:updated', {id: user.id});
				});
			}
			// UserResource.$update(user);
			// if (!editedUser) {
			// 	users.push(user);
			// }
		},
		get: function(id, callback) {
			$log.log('get user: ' + id);
      var user = new UserResource.get({'id': id}, function() {
        $log.log('get user: ' + JSON.stringify(user));
        callback(user);
      });
    }
	};
}]);
