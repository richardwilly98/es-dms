'use strict';

describe('Service: esdmsAuthenticationService', function () {

  // load the service's module
  beforeEach(module('esDmsSiteApp'));

  // instantiate service
  var esdmsAuthenticationService;
  beforeEach(inject(function (_esdmsAuthenticationService_) {
    esdmsAuthenticationService = _esdmsAuthenticationService_;
  }));

  it('should do something', function () {
    expect(!!esdmsAuthenticationService).toBe(true);
  });

});
