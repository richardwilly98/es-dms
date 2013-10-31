'use strict';

describe('Controller: DocumentsFileUploadCtrl', function () {

  // load the controller's module
  beforeEach(module('esDmsSiteApp'));

  var DocumentsFileUploadCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    DocumentsFileUploadCtrl = $controller('DocumentsFileUploadCtrl', {
      $scope: scope
    });
  }));

  /*it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });*/
});
