'use strict';

describe('Controller: DocumentsNewCtrl', function () {

  // load the controller's module
  beforeEach(module('esDmsSiteApp'));

  var DocumentsNewCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    DocumentsNewCtrl = $controller('DocumentsNewCtrl', {
      $scope: scope
    });
  }));

  /*it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });*/
});
