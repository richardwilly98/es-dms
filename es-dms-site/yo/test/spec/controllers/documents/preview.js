'use strict';

describe('Controller: DocumentPreviewCtrl', function () {

  // load the controller's module
  beforeEach(module('esDmsSiteApp'));

  var DocumentPreviewCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    DocumentPreviewCtrl = $controller('DocumentPreviewCtrl', {
      $scope: scope
    });
  }));

  /*it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });*/
});
