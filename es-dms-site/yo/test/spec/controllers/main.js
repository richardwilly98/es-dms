'use strict';

describe('Controller: mainController', function () {

  // load the controller's module
  beforeEach(module('esDmsSiteApp'));

  var mainController, scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    mainController = $controller('mainController', {
      $scope: scope
    });
  }));

  /*
  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
  */
});
