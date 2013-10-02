'use strict';

describe('Service: ratingService', function () {

  // load the service's module
  beforeEach(module('esDmsSiteApp'));

  // instantiate service
  var ratingService;
  beforeEach(inject(function (_ratingService_) {
    ratingService = _ratingService_;
  }));

  it('should do something', function () {
    expect(!!ratingService).toBe(true);
  });

});
