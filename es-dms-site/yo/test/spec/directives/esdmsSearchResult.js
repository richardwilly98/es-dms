'use strict';

describe('Directive: esdmsSearchResult', function () {

  // load the directive's module
  beforeEach(module('esDmsSiteApp'));

  var element,
    scope;

  beforeEach(inject(function ($rootScope) {
    scope = $rootScope.$new();
  }));

  it('should make hidden element visible', inject(function ($compile) {
    element = angular.element('<esdms-search-result></esdms-search-result>');
    element = $compile(element)(scope);
  }));
});
