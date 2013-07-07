'use strict';

describe('Controller: SearchOptionsCtrl', function () {

  // load the controller's module
  beforeEach(module('esDmsSiteApp'));

  var SearchOptionsCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    SearchOptionsCtrl = $controller('SearchOptionsCtrl', {
      $scope: scope
    });
  }));

  /*it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });*/
});
