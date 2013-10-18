'use strict';

describe('Service: processService', function () {

  // load the service's module
  beforeEach(module('esDmsSiteApp'));

  // instantiate service
  var processService;
  beforeEach(inject(function (_processService_) {
    processService = _processService_;
  }));

  it('should do something', function () {
    expect(!!processService).toBe(true);
  });

});
