'use strict';

describe('Controller: DocumentsVersionCtrl', function () {

  // load the controller's module
  beforeEach(module('esDmsSiteApp'));

  var DocumentsVersionCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    DocumentsVersionCtrl = $controller('DocumentsVersionCtrl', {
      $scope: scope
    });
  }));

  // it('should attach a list of awesomeThings to the scope', function () {
  //   expect(scope.awesomeThings.length).toBe(3);
  // });
});
