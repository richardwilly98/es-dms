simpleApp.controller('MainCtrl', function ($scope, $window, $location) {
    $scope.$location = $location;
});

simpleApp.controller('simpleController', function ($scope, userService) {
    $scope.customers = [];

    init();

    function init() {
        $scope.customers = userService.query({ verb: 'find', name: '*' });
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

simpleApp.controller('loginController', function ($scope, $cookieStore, authenticationService) {

    $scope.login = function() {
    	console.log('username: ' + $scope.username);
    	authenticationService.login($scope.username, $scope.password, function(data) {
    		console.log(data);
    	});
    };
});

simpleApp.controller('documentController', function ($scope, documentService) {
    $scope.documents = [];

    init();

    function init() {
        $scope.documents = documentService.query({ verb: 'find', name: '*' });
    }
    
    $scope.search = function() {
    	$scope.documents = documentService.query({ verb: 'search', name: $scope.criteria });
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

simpleApp.controller('newDocumentCtrl', function ($scope) {
	$scope.alert = {};
	$scope.newDocument = {};
    $scope.uploadComplete = function (content, completed) {
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
            // Look for way to clear the input[type=file] element
        }
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
	
	$scope.showLogin = function() {
		var login = $element.find('login-holder');
		login.show();
	};
});
