'use strict';

var systemSettings = {id: 'es-dms', name:'es-dms', 
  attributes: {
    'preview.length': 2048
  }
};
var validate = {id: "admin", url: "http://localhost:18080"};
var user = {id: "admin", login: "admin"};
var doc = {id: 3, name: "dummy", attributes: {author: "admin"}};
var doc2 = {id: 4, name: "dummy", attributes: {author: "admin"}, tags: ["tag1", "tag2"]};
var documents = [
  { id: 1, name: "Entry 1" }, 
  { id: 2, name: "Entry 2" }
  ];

describe('Service: documentService', function () {

  var resource, $httpBackend;

  // instantiate service
  var documentService;

  // Load required modules
  beforeEach(angular.mock.module("ngResource"));
  
  // Load the service's module
  beforeEach(angular.mock.module("esDmsSiteApp"));

  // Load dependencies
  beforeEach(function() {
    inject(function($injector) {
        resource = $injector.get('$resource');
        $httpBackend = $injector.get('$httpBackend');
        documentService = $injector.get('documentService');
    });
  });

  beforeEach(function(){
    $httpBackend.whenPOST('api/auth/_validate').respond(validate);
    $httpBackend.whenGET('api/parameters/es-dms').respond(systemSettings);
    $httpBackend.whenGET('api/users/admin').respond(user);
    $httpBackend.whenGET('api/documents/search/dummy?fi=0&ps=20').respond(documents);
    $httpBackend.whenGET('api/documents/3').respond(doc);
    $httpBackend.whenPUT('api/documents/3').respond(200, '');
    $httpBackend.whenGET('api/documents/3/_metadata').respond(doc);
    $httpBackend.whenPOST('api/documents/3/_checkout').respond(204, '');
    $httpBackend.whenPOST('api/documents/3/tags/tag1').respond(201, '');

    $httpBackend.whenGET('api/documents/4').respond(doc2);
    $httpBackend.whenPUT('api/documents/4').respond(200, '');
    $httpBackend.whenGET('api/documents/4/_metadata').respond(doc2);
    $httpBackend.whenDELETE('api/documents/4/tags/tag2').respond(204, '');
  });

  afterEach(function() {
    $httpBackend.verifyNoOutstandingExpectation();
    $httpBackend.verifyNoOutstandingRequest();
  });

  it('should call documentService find method', function() {
    $httpBackend.expectGET('api/documents/search/dummy?fi=0&ps=20');
    documentService.find(0, 20, 'dummy', function(data) {
      expect(data.length).toBe(2);
    });
    $httpBackend.flush();
  });

  it('should call documentService addTag method', function() {
    $httpBackend.expectGET('api/documents/3/_metadata');
    $httpBackend.expectPOST('api/documents/3/tags/tag1');
    documentService.addTag(3, 'tag1', function(data) {
      expect(data.tags).toContain('tag1');
    });
    $httpBackend.flush();
  });

  it('should call documentService removeTag method', function() {
    $httpBackend.expectGET('api/documents/4/_metadata');
    $httpBackend.expectDELETE('api/documents/4/tags/tag2');
    documentService.removeTag(4, 'tag2', function(data) {
      expect(data.tags).not.toContain('tag2');
      expect(data.tags).toContain('tag1');
    });
    $httpBackend.flush();
  });

  /*
  it('should call documentService checkout method', function() {
    $httpBackend.expectGET('api/documents/3');
    $httpBackend.expectPOST('api/documents/3/checkout');
    spyOn(documentService, 'checkout');
    documentService.checkout(3);
    //$httpBackend.flush();
    expect(documentService.checkout).toHaveBeenCalled();
    $httpBackend.flush();
  });
  */

});
