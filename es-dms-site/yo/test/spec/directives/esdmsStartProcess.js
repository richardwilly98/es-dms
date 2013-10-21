'use strict';

describe('Directive: esdmsStartProcess', function () {

  // load the directive's module
  beforeEach(module('esDmsSiteApp'));

  var element,
    scope;

  beforeEach(inject(function ($rootScope) {
    scope = $rootScope.$new();
  }));

  it('should make hidden element visible', inject(function ($compile) {
    element = angular.element('<esdms-start-process></esdms-start-process>');
    element = $compile(element)(scope);
    // expect(element.text()).toBe('this is the esdmsStartProcess directive');
  }));
});
