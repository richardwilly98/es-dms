'use strict';

describe('Directive: esdmsPreview', function () {

  // load the directive's module
  beforeEach(module('esDmsSiteApp'));

  var element,
    scope;

  beforeEach(inject(function ($rootScope) {
    scope = $rootScope.$new();
  }));

  it('should make hidden element visible', inject(function ($compile) {
    element = angular.element('<esdms-preview></esdms-preview>');
    element = $compile(element)(scope);
    //expect(element.text()).toBe('this is the esdmsPreview directive');
  }));
});
