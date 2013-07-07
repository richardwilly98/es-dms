'use strict';

describe('Service: roleService', function () {

  // load the service's module
  beforeEach(module('esDmsSiteApp'));

  // instantiate service
  var roleService;
  beforeEach(inject(function (_roleService_) {
    roleService = _roleService_;
  }));

  it('should do something', function () {
    expect(!!roleService).toBe(true);
  });

});
