'use strict';

describe('Directive: esdmsAuthenticationDirective', function () {
  beforeEach(module('esDmsSiteApp'));

  var element;

  it('should make hidden element visible', inject(function ($rootScope, $compile) {
    element = angular.element('<esdms-authentication-directive></esdms-authentication-directive>');
    element = $compile(element)($rootScope);
    //expect(element.text()).toBe('this is the esdmsAuthenticationDirective directive');
  }));
});
