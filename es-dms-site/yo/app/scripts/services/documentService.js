'use strict';

esDmsSiteApp.service('documentService', ['$sce', '$log', '$rootScope', '$resource', '$http',
  function documentService($sce, $log, $rootScope, $resource, $http) {
    var documentResource = $resource('api/documents/:id/:action/:parameter' , {id:'@id'}, {
        checkout: {method:'POST', params: {action: 'checkout'}},
        checkin: {method:'POST', params: {action: 'checkin'}},
        preview: {method:'GET', params: {action: 'preview'}},
        metadata: {method:'GET', params: {action: 'metadata'}},
        audit: {method:'GET', params: {action: 'audit'}},
        markDeleted: {method:'POST', params: {action: 'deleted'}},
        undelete: {method:'POST', params: {action: 'undelete'}},
        update: {method:'PUT', params: {}}
      });

    var currentDocumentId = null;
    return {
      find: function(first, pageSize, criteria, callback) {
        $log.log('Document search ' + first + ' - ' + pageSize + ' - ' + criteria);
        $http.get('api/documents/search/' + criteria + '?fi=' + first + '&ps=' + pageSize).success(function (data/*, status*/) {
          callback(data);
        });
      },
      showDetails: function(id) {
        $log.log('showDetails document: ' + id);
        if (id === 'new') {
          currentDocumentId = null;
        } else {
          currentDocumentId = id;
        }
        $rootScope.$broadcast('document:showDetails');
      },
      current: function() {
        return currentDocumentId;
      },
      checkout: function(id) {
        $log.log('checkout document: ' + id);
        var doc = new documentResource.get({'id': id});
        doc.$checkout({'id': id});
      },
      checkin: function(id) {
        $log.log('checkin document: ' + id);
        var doc = new documentResource.get({'id': id});
        doc.$checkin({'id': id});
      },
      metadata: function(id, callback) {
        $log.log('metadata document: ' + id);
        var document = new documentResource.metadata({'id': id}, function() {
          $log.log('get document: ' + JSON.stringify(document));
          callback(document);
        });
      },
      audit: function(id, callback) {
        $log.log('audit document: ' + id);
        var document = new documentResource.audit({'id': id}, function() {
          $log.log('get document: ' + JSON.stringify(document));
          callback(document);
        });
      },
      addTag: function(id, tag, callback) {
        $log.log('addTag document: ' + id + ' - tag: ' + tag);
        var document = new documentResource.metadata({'id': id}, function() {
          $log.log('get document: ' + JSON.stringify(document));
          if (document.tags === undefined) {
            document.tags = [];
          }
          document.tags.push(tag);
          $log.log('save document: ' + JSON.stringify(document));
          document.$update();
          callback(document);
        });
      },
      removeTag: function(id, tag, callback) {
        $log.log('removeTag document: ' + id + ' - tag: ' + tag);
        var document = new documentResource.metadata({'id': id}, function() {
          $log.log('get document: ' + JSON.stringify(document));
          if (document.tags === undefined) {
            document.tags = [];
          }
          //document.tags.splice(tag, 1);
          document.tags = _.without(document.tags, tag);
          $log.log('save document: ' + JSON.stringify(document));
          document.$update();
          callback(document);
        });
      },
      remove: function(id, callback) {
        $log.log('delete document: ' + id);
        var doc = new documentResource.get({'id': id});
        doc.$markDeleted({'id': id}, function() {
          doc.$delete({'id': id}, function (response) {
            callback(response);
          });
        });
        // doc.$delete({'id': id}, callback);
        // doc.$delete({'id': id}, 
        //   function(response) {
        //     $log.log('success: ' + JSON.stringify(response));
        //   },
        //   function(response) {
        //     $log.log('error: ' + JSON.stringify(response));
        //   }
        // );
      },
      preview: function(id, criteria, callback) {
        $log.log('preview document: ' + id + ' - criteria: ' + criteria);
        var response = documentResource.preview({'id': id, 'cr': criteria/*, 'fs': 100*/}, function () {
          callback($sce.trustAsHtml(response.content));
        });
      }
    };
  }]);
