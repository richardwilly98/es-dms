'use strict';

esDmsSiteApp.directive('esdmsTagging', ['$log', '$compile', function ($log, $compile) {
	$log.log('Start esdmsTagging');
  return {
    restrict: 'E',
    scope: { id: '=', tags: '='},
    compile: function() {
      return {
        pre: function(scope) {
          
          scope.select2options = {
            'simple_tags': true,
            'tags': [],
            'tokenSeparators': [',', ' ']
          };

          scope.$watch('tags', function(newValue, oldValue) {
            function add(tag) {
              $log.log('add tag: ' + tag + ' for doc: ' + scope.id);
              scope.$emit('document:addtag', {'id': scope.id, 'tag': tag});
            }
            // This is the ng-click handler to remove an item
            function remove(tag) {
              scope.$emit('document:removetag', {'id': scope.id, 'tag': tag});
            }

            if (newValue !== undefined && oldValue!== undefined && newValue !== oldValue) {
              var tag;
              $log.log('select2tags has been updated - newValue: ' + newValue + ' - oldValue: ' + oldValue);
              if (newValue.length === 0 && oldValue.length > 0) {
                tag = _.first(oldValue);
                $log.log('remove tag ' + tag);
                remove(tag);
              } else if (newValue.length > oldValue.length) {
                tag = _.first(_.difference(newValue, oldValue));
                $log.log('add tag ' + tag);
                add(tag);
              } else if (newValue.length < oldValue.length) {
                tag = _.first(_.difference(oldValue, newValue));
                $log.log('remove tag ' + tag);
                remove(tag);
              }
            }
          }, true);
        },
        post : function(scope, element) {
          var template = '<input ui-select2="select2options" ng-model="tags" />';
          element.replaceWith($compile(template)(scope));
        }
      };
    }
  };
}]);
