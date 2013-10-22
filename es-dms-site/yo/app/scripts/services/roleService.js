'use strict';

esDmsSiteApp.service('roleService', ['$log', '$rootScope', '$resource', '$http', function roleService($log, $rootScope, $resource, $http) {
	var RoleResource = $resource('api/roles/:id/:action', { }, {
		update: {method: 'PUT'},
		create: {method: 'POST'}
	});
	var RoleTypeResource = $resource('api/role-types', { }, {
	});
	var roles = [];
	var roleTypes = [];
	var editedRole = null;
	return {
		roleTypes: function(callback) {
			if (roleTypes.length === 0) {
				var types = RoleTypeResource.query(function(){
					$log.log('get roleTypes: ' + JSON.stringify(types));
					roleTypes = types;
	        callback(types);
				});
			} else {
				callback(roleTypes);
			}
		},
		search: function(criteria, callback) {
			$http.get('api/roles/search/' + criteria).success(function (data) {
				roles = data.items;
				callback(data);
			});
		},
		edit: function(id) {
			$log.log('edit role: ' + id);
			if (id === 'new') {
				editedRole = null;
			} else {
				editedRole = id;
			}
			$rootScope.$broadcast('role:edit');
		},
		currentRole: function(callback) {
			if (editedRole) {
				var role = new RoleResource.get({'id': editedRole}, function(){
					callback(role);
				});
			} else {
				return callback(new RoleResource());
			}
		},
		save: function(role, isNew) {
			$log.log('save role: ' + JSON.stringify(role));
			if (isNew) {
				role.$create({}, function() {
					$rootScope.$broadcast('role:updated', {id: role.id});
				});
			} else {
				role.$update({id: role.id}, function() {
					$rootScope.$broadcast('role:updated', {id: role.id});
				});
			}
		}
	};
}]);
