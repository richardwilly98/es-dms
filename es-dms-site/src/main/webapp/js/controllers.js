esDmsApp.controller('mainController', function ($log, $scope, $window, $location, sharedService) {
    $scope.$location = $location;
    $scope.username = '';
	$scope.$on('handleBroadcast', function() {
		$log.log('Receive brodcast message');
        //$scope.showLogout = sharedService.message.logout;
		$scope.username = sharedService.message.user;
    }); 
});

esDmsApp.controller('adminController', function ($scope) {
});

esDmsApp.controller('userController', function ($scope, userService) {
    $scope.users = [];
    $scope.totalHits = 0;
    $scope.elapsedTime = 0;
    var currentUser = null;
    
    init();

    function init() {
    }

	function getIndexOf(id) {
		for (var i in $scope.users) {
			if ($scope.users[i].id == id) {
				return i;
			}
		}
	}

	$scope.search = function() {
		var result = userService.search($scope.criteria, function(result) {
			$scope.users = result.items;
			$scope.totalHits = result.totalHits;
			$scope.elapsedTime = result.elapsedTime;
		});
    };
    
    $scope.edit = function(id) {
		userService.edit(id);
    };
    
    $scope.delete = function(id) {
		userService.delete(id);
		var index = getIndexOf(id);
		if (index) {
			$scope.users.splice(index, 1);
		}
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

esDmsApp.controller('roleController', function ($scope, roleService) {
    $scope.roles = [];
    $scope.totalHits = 0;
    $scope.elapsedTime = 0;
    var currentRole = null;
    
    init();

    function init() {
    }

    $scope.search = function() {
		var result = roleService.search($scope.criteria, function(result) {
			$scope.roles = result.items;
			$scope.totalHits = result.totalHits;
			$scope.elapsedTime = result.elapsedTime;
		});
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

esDmsApp.controller('userEditController', function ($scope, $rootScope, $http, userService) {
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

esDmsApp.controller('roleEditController', function ($scope, $rootScope, roleService) {
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

esDmsApp.controller('simpleController', function ($scope, userService) {
    $scope.customers = [];

    init();

    function init() {
		userService.find('*', function(result) {
			$scope.customers = result.items;
		});
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

esDmsApp.controller('loginController', function ($log, $scope, /* $cookieStore, */ authenticationService, authService, sharedService) {

    $scope.shouldBeOpen = true;

    $scope.login = function() {
		$log.log('loginController - login');
		authenticationService.login($scope.username, $scope.password, $scope.rememberMe, function(data) {
			$log.log('data: ' + data);
			if (data.status === 'AUTHENTICATED') {
				authService.loginConfirmed();
				var token = data.token;
				$log.info(token);
				$scope.shouldBeOpen = false;
				sharedService.prepForBroadcast({logout: true});
				sharedService.prepForBroadcast({user: $scope.username});
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

esDmsApp.controller('documentController', function ($log, $scope, documentService, searchService) {
    $scope.alerts = [];
    $scope.documents = [];
	$scope.facet = null;
    $scope.facets = [];
    $scope.totalHits = 0;
    $scope.elapsedTime = 0;
    $scope.maxPages = 10;
    $scope.totalPages = 0;
    $scope.currentPage = 1;
    $scope.pageSize = 12;
    $scope.pageSizeList = [12, 24, 48, 96];
    $scope.newtag = {};
    $scope.terms = [];
    
    var currentDocument = null;

    init();

    function init() {
    }
    
    $scope.mySearch = function() {
		$log.log('mySearch');
		if ($scope.criteria === '' || $scope.criteria === '*') {
			$scope.alerts.push({ msg: "Empty or wildcard not allowed" });
			$scope.documents = [];
		} else {
			find(0,  'document.attributes.author: "' + $scope.criteria + '"', true);
		}
	};
    
    $scope.search = function(/*term*/) {
		$log.log('search');
		if ($scope.criteria === '' || $scope.criteria === '*') {
			$scope.alerts.push({ msg: "Empty or wildcard not allowed" });
			$scope.documents = [];
		} else {
			$scope.facet = 'tags';
			find(0, $scope.criteria, /*term,*/ true);
		}
    };

    function find(first, criteria, /*term,*/ updatePagination) {
		$log.log('find - terms: ' + $scope.terms);
		var filters = getFilter(/*term*/);
		searchService.facetedSearch(first, $scope.pageSize, criteria, $scope.facet, filters, function(result) {
			if (updatePagination) {
				setPagination(result);
			}
			$scope.documents = result.items;
			$scope.totalHits = result.totalHits;
			$scope.elapsedTime = result.elapsedTime;
			$scope.facets = result.facets[$scope.facet];
			for (var i in $scope.facets[$scope.facet].terms) {
				
			}
		});
    }
    
    function getFilter() {
		if ($scope.facet === undefined || $scope.terms === [] || $scope.terms.length === 0) {
			return null;
		}
		var filter = {};
		filter[$scope.facet] = $scope.terms;
		return filter;
    }

    function setPagination(result) {
		var pageSize = result.pageSize;
		var totalHits = result.totalHits;
		var firstIndex = result.firstIndex;
		$scope.totalPages = Math.ceil(totalHits / pageSize);
		$scope.currentPage = 1 + (firstIndex / pageSize);
		$log.log('totalPages: ' + $scope.totalPages + ' - currentPage: ' + $scope.currentPage);
    }
    
	function getDocument(id) {
		var documents = $scope.documents;
		for (var i in documents) {
			if (documents[i].id == id) {
				return documents[i];
			}
		}
	}
	function getIndexOf(id) {
		for (var i in $scope.documents) {
			if ($scope.documents[i].id == id) {
				return i;
			}
		}
	}
    
    $scope.setPage = function () {
		$log.log('setPage');
		if ($scope.criteria === undefined ) {
			return;
		}
        find( ($scope.currentPage - 1) * $scope.pageSize, $scope.criteria );
      };
      
    $scope.$watch('currentPage', $scope.setPage );
      
    $scope.$on('document:addtag', function(evt, args) {
		if (args.id === undefined || args.tag === undefined) {
			return;
		}
		$log.log('*** addTag: ' + args.id + ' - ' + args.tag);
		var id = args.id;
		var tag = args.tag;
		var document = getDocument(id);
		documentService.addTag(id, tag, function(doc) {
			var index = getIndexOf(id);
			$scope.documents[index] = doc;
		});
      });

    $scope.$on('document:removetag', function(evt, args) {
		if (args.id === undefined || args.tag === undefined) {
			return;
		}
		$log.log('*** removetag: ' + args.id + ' - ' + args.tag);
		var id = args.id;
		var tag = args.tag;
		var document = getDocument(id);
		documentService.removeTag(id, tag, function(doc) {
			var index = getIndexOf(id);
			$scope.documents[index] = doc;
		});
      });
    
    $scope.$on('search:applyfacet', function(evt, args) {
		if (args.term === undefined || args.selected === undefined) {
			return;
		}
		$log.log('*** applyfacet: ' + args.term + ' - ' + args.selected);
		if (args.selected) {
			$scope.terms.push(args.term);
		} else {
			for (var i in $scope.terms) {
				if ($scope.terms[i] == term) {
					$scope.terms.splice(i, 1);
					break;
				}
			}
		}
		find(0, $scope.criteria, /*term,*/ true);
      });

    $scope.closeAlert = function (index) {
        $scope.alerts.splice(index, 1);
    };

    $scope.edit = function(id) {
		documentService.edit(id);
    };

    $scope.checkout = function(id) {
		documentService.checkout(id);
		var document = getDocument(id);
		if (document) {
			document.attributes.status = 'L';
		}
    };

    $scope.checkin = function(id) {
		documentService.checkin(id);
		var document = getDocument(id);
		if (document) {
			document.attributes.status = 'A';
		}
    };

    $scope.delete = function(id) {
		documentService.delete(id);
		var index = getIndexOf(id);
		if (index) {
			$scope.documents.splice(index, 1);
		}
    };
    
    $scope.preview = function(id) {
		var document = getDocument(id);
		if (currentDocument != document) {
			documentService.preview(id, $scope.criteria, function(response) {
				$log.log('Preview document - ' + document.id);
					document.preview = response;
			});
		} else {
			$log.log('Do not fetch preview again!');
		}
		currentDocument = document;
    };
});

esDmsApp.controller('AlertDemoCtrl', function ($scope) {
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

esDmsApp.controller('newDocumentController', function ($log, $scope) {
	$scope.showAlert = false;
	$scope.alert = {};
	$scope.newDocument = {};
    $scope.uploadComplete = function (content, completed) {
		$log.log('uploadComplete ' + content + ' - ' + completed);
        if (completed && content.length > 0)
        {
			var response = JSON.parse(content);
			$log.log(response);
            
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

esDmsApp.controller('modalCtrl', function ($log, $scope) {
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
		$log.log('broadcast - event:auth-loginRequired');
		$scope.$broadcast('event:auth-loginRequired');
	};
	
});

esDmsApp.controller('navbarController', function ($scope, sharedService, authenticationService) {
	$scope.showLogout = true;
	$scope.$on('handleBroadcast', function() {
        //$scope.showLogout = sharedService.message.logout;
    }); 
	
	$scope.tabs = [
		{ "view": "/search-view", title: "Search" },
		{ "view": "/my-documents-view", title: "My documents" },
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
	};
});

esDmsApp.controller('fileUploadController', function ($log, $scope, $http, fileUpload) {
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
		$log.log('fileuploaddone - ' + e + ' - ' + data);
    };
});

esDmsApp.controller('fileDestroyController', function ($scope, $http) {
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


