'use strict';

describe('Service: messagingService', function () {

  // load the service's module
  beforeEach(module('esDmsSiteApp'));

  // instantiate service
  var messagingService;
  beforeEach(inject(function (_messagingService_) {
    messagingService = _messagingService_;
  }));

  it('should do something', function () {
    expect(!!messagingService).toBe(true);
  });

});
