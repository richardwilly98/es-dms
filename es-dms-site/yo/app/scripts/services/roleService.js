'use strict';

esDmsSiteApp.service('roleService', ['$log', '$rootScope', '$resource', '$http', function roleService($log, $rootScope, $resource, $http) {
	var resource = $resource('api/roles/:verb/:name', {}, {});
	var roles = [];
	var editedRole = null;
	return {
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
		currentRole: function() {
			if (editedRole) {
				for (var i in roles) {
					if (roles[i].id === editedRole) {
						return roles[i];
					}
				}
			} else {
				return {};
			}
		},
		save: function(role) {
			$log.log('save role: ' + role);
			resource.save(role);
			if (!editedRole) {
				roles.push(role);
			}
		}
	};
}]);
