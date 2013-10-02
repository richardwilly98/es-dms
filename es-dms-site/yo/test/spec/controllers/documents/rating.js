'use strict';

describe('Controller: DocumentsRatingCtrl', function () {

  // load the controller's module
  beforeEach(module('esDmsSiteApp'));

  var DocumentsRatingCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    DocumentsRatingCtrl = $controller('DocumentsRatingCtrl', {
      $scope: scope
    });
  }));

  it('should attach an initial rating to the scope', function () {
    expect(scope.rating.score).toBe(0);
    expect(scope.rating.overStar).toBe(false);
  });

  it('should change the rating to the scope', function () {
    scope.hoveringRating(5);
    expect(scope.rating.score).toBe(5);
    expect(scope.rating.overStar).toBe(true);
  });
});
