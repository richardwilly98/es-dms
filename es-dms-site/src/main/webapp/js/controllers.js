simpleApp.controller('MainCtrl', function ($scope, $window, $location) {
    $scope.$location = $location;
});

simpleApp.controller('adminController', function ($scope) {
});

simpleApp.controller('userController', function ($scope, userService) {
    $scope.users = [];
    var currentUser = null;
    
    init();

    function init() {
    }

    $scope.find = function() {
    	$scope.users = userService.find($scope.criteria);
    };
    
    $scope.edit = function(id) {
    	userService.edit(id);
    };
    
    $scope.add = function () {
// var user = new userService();
// user.name = $scope.name;
// user.city = $scope.city;
// user.email = $scope.email
// user.$save();
// $scope.user.push(user);
    };
});

simpleApp.controller('roleController', function ($scope, roleService) {
    $scope.roles = [];
    var currentRole = null;
    
    init();

    function init() {
    }

    $scope.find = function() {
    	$scope.roles = roleService.find($scope.criteria);
    };
    
    $scope.edit = function(id) {
    	roleService.edit(id);
    };
    
    $scope.add = function () {
// var user = new userService();
// user.name = $scope.name;
// user.city = $scope.city;
// user.email = $scope.email
// user.$save();
// $scope.user.push(user);
    };
});

simpleApp.controller('userEditController', function ($scope, $rootScope, $http, userService) {
	$scope.user = null;
	$scope.newUser = false;
	$scope.uid = '';
	$scope.user = {};
	$scope.pw1 = '';
	$scope.pw2 = '';
	$scope.pwError = false;
	$scope.incomplete = false;
	  
	$rootScope.$on('user:edit', function() {
		var editUser = userService.currentUser();
	    if (editUser.id) {
	    	$scope.user = editUser;
	    	$scope.newUser = false;
	    	// $scope.uid = editUser.id;
// $scope.user.roles = [{id: 'reader', name: 'Reader'}, {id: 'writer', name:
// 'Writer'}];
	    } else {
	    	$scope.newUser = true;
	    	$scope.incomplete = true;
	    	$scope.user = {};
	    	$scope.pw1 = '';
	    	$scope.pw2 = '';
	    }
	});
	
	$scope.save = function() {
		if ($scope.newUser) {
			$scope.user.password = $scope.pw1;
		}
		userService.save($scope.user);
	};
	
	$scope.$watch('pw1', function() {
		if ($scope.pw1 !== $scope.pw2) {
			$scope.pwError = true;
		} else {
			$scope.pwError = false;
	    }
	    $scope.incompleteTest();
	});
	
	$scope.$watch('pw2', function() {
		if ($scope.pw1 !== $scope.pw2) {
			$scope.pwError = true;
		} else {
			$scope.pwError = false;
		}
	    $scope.incompleteTest();
	});

	$scope.$watch('username', function() {
		$scope.incompleteTest();
	});
	  
	$scope.incompleteTest = function() {
		if ($scope.newUser) {
			if (!$scope.user.name.length || !$scope.pw1.length || !$scope.pw2.length) {
				$scope.incomplete = true;
			} else {
				$scope.incomplete = false;
			}
		} else {
			$scope.incomplete = false;
	    }
	};
});

simpleApp.controller('roleEditController', function ($scope, $rootScope, roleService) {
	$scope.role = {};
	$rootScope.$on('role:edit', function() {
		var editRole = roleService.currentRole();
	    if (editRole.id) {
	    	$scope.role = editRole;
	    	$scope.newRole = false;
	    } else {
	    	$scope.newRole = true;
	    	$scope.incomplete = true;
	    	$scope.role = {};
	    }
	});
	
	$scope.save = function() {
		roleService.save($scope.role);
	};
});

simpleApp.controller('simpleController', function ($scope, userService) {
    $scope.customers = [];

    init();

    function init() {
        // $scope.customers = userService.query({ verb: 'find', name: '*' });
    	$scope.customers = userService.find('*');
    }
    $scope.addCustomer = function () {
        var user = new userService();
        user.name = $scope.newCustomer.name;
        user.city = $scope.newCustomer.city;
        user.$save(); 
        $scope.customers.push({
            name: $scope.newCustomer.name,
            city: $scope.newCustomer.city
        });
    };
});

simpleApp.controller('loginController', function ($scope, /* $cookieStore, */ authenticationService, authService, sharedService) {

    $scope.shouldBeOpen = true;

    $scope.login = function() {
    	console.log('loginController - login');
    	authenticationService.login($scope.username, $scope.password, $scope.rememberMe, function(data) {
    		console.log('data: ' + data);
// var response = JSON.parse(data);
// console.log('response: ' + response);
    		if (data.status === 'AUTHENTICATED') {
        		authService.loginConfirmed();
        		var token = data.token;
        		console.log(token);
// $cookieStore.put('_ES_DMS_TICKET', unescape(token))
// console.log('Cookie ES_DMS_TICKET: ' + $cookieStore.get('_ES_DMS_TICKET'));
        		$scope.shouldBeOpen = false;
        		sharedService.prepForBroadcast({logout: true});
    		}
    	});
    };

    $scope.open = function () {
        $scope.shouldBeOpen = true;
    };

    $scope.close = function () {
        $scope.shouldBeOpen = false;
    };
});

simpleApp.controller('documentController', function ($scope, documentService) {
    $scope.alerts = [];

    $scope.documents = [];
    var currentDocument = null;

    init();

    function init() {
    }
    
    $scope.search = function() {
    	if ($scope.criteria == '' || $scope.criteria == '*') {
    		$scope.alerts.push({ msg: "Empty or wildcard not allowed" });
    		$scope.documents = [];
    	} else {
        	$scope.documents = documentService.find($scope.criteria);
    	}
    }

    $scope.closeAlert = function (index) {
        $scope.alerts.splice(index, 1);
    };

    $scope.edit = function(id) {
    	documentService.edit(id);
    };

    $scope.checkout = function(id) {
    	documentService.checkout(id);
    	var document = documentService.getDocument(id);
    	if (document) {
    		document.attributes.status = 'L';
    	}
    };

    $scope.checkin = function(id) {
    	documentService.checkin(id);
    	var document = documentService.getDocument(id);
    	if (document) {
    		document.attributes.status = 'A';
    	}
    };

    $scope.delete = function(id) {
    	documentService.delete(id);
    	var index = documentService.getIndexOf(id);
    	if (index) {
    		$scope.documents.splice(index, 1);
    	}
    };
    
    $scope.preview = function(id) {
		var document = documentService.getDocument(id);
		if (currentDocument != document) {
	    	documentService.preview(id, $scope.criteria, function(response) {
	        		console.log('Preview document - ' + document.id);
	        		document.preview = response;
	    	});
		} else {
			console.log('Do not fetch preview again!');
		}
		currentDocument = document;
    }
});

simpleApp.controller('AlertDemoCtrl', function ($scope) {
    $scope.alerts = [
    { type: 'error', msg: 'Oh snap! Change a few things up and try submitting again.' },
    { type: 'success', msg: 'Well done! You successfully read this important alert message.' }
  ];

    $scope.addAlert = function () {
        $scope.alerts.push({ msg: "Another alert!" });
    };

    $scope.closeAlert = function (index) {
        $scope.alerts.splice(index, 1);
    };

});

simpleApp.controller('newDocumentController', function ($scope) {
	$scope.showAlert = false;
	$scope.alert = {};
	$scope.newDocument = {};
    $scope.uploadComplete = function (content, completed) {
    	console.log('uploadComplete ' + content + ' - ' + completed);
        if (completed && content.length > 0)
        {
        	var response = JSON.parse(content);
        	console.log(response);
            
            // Clear form (reason for using the 'ng-model' directive on the
			// input elements)
            $scope.newDocument.name = '';
            $scope.newDocument.date = '';
            // $scope.newDocument.id = response.id;
            $scope.alert = { type: 'success', msg: 'New document created #' + response.id};
            $scope.showAlert = true;
            // Look for way to clear the input[type=file] element
        }
    };
    $scope.closeAlert = function() {
    	$scope.showAlert = false;
    	$scope.alert = {};
    };
});

simpleApp.controller('modalCtrl', function ($scope) {
	$scope.open = function() {
		$scope.shouldBeOpen = true;
	};
	$scope.close = function () {
		$scope.closeMsg = 'I was closed at: ' + new Date();
	    $scope.shouldBeOpen = false;
	};
	$scope.opts = {
			backdropFade: true,
			dialogFade:true
	};

	$scope.testHttp401 = function() {
		console.log('broadcast - event:auth-loginRequired');
		$scope.$broadcast('event:auth-loginRequired');
	};
	
});

simpleApp.controller('navbarController', function ($scope, sharedService, authenticationService) {
	$scope.showLogout = false;
	$scope.$on('handleBroadcast', function() {
        $scope.showLogout = sharedService.message.logout;
    }); 
	
	$scope.tabs = [
	    { "view": "/search-view", title: "Search" },
	    { "view": "/edit-view", title: "Edit" },
        { "view": "/view1", title: "View #1" },
        { "view": "/view2", title: "View #2" },
        { "view": "/view3", title: "Test View"},
        { "view": "/view4", title: "View #4" }
    ];
	
	$scope.adminTabs = [
		 { "view": "/admin/users", title: "Users" },
		 { "view": "/admin/roles", title: "Roles" }
	];
	
	$scope.logout = function() {
		authenticationService.logout();
	}
});

simpleApp.controller('fileUploadController', function ($scope, $http, fileUpload) {
	var url = 'api/documents/upload';
	$scope.loadingFiles = false;
	$scope.options = {
			url: url
	};
	$http.get(url)
		.then(
				function (response) {
					$scope.loadingFiles = false;
					$scope.queue = response.data.files;
				},
				function () {
					$scope.loadingFiles = false;
				}
		);
	fileUpload.fileuploaddone = function(e, data) {
    	console.log('fileuploaddone - ' + e + ' - ' + data);    	
    };
});

simpleApp.controller('fileDestroyController', function ($scope, $http) {
	var file = $scope.file, state;
	if (file.url) {
		file.$state = function () {
			return state;
		};
		file.$destroy = function () {
			state = 'pending';
			return $http({
				url: file.delete_url,
				method: file.delete_type
			}).then(
					function () {
						state = 'resolved';
						$scope.clear(file);
					},
					function () {
						state = 'rejected';
					}
			);
		};
	} else if (!file.$cancel) {
		file.$cancel = function () {
			$scope.clear(file);
		};
	}
});


