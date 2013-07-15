'use strict';

esDmsSiteApp.directive('esdmsTagging', ['$log', '$compile', function ($log, $compile) {
	$log.log('Start esdmsTagging');
  return {
    restrict: 'E',
    scope: { id: '=', tags: '='},
    compile: function() {
      return {
        pre: function(scope) {
          // transform tags (array of string) in array of object (with id, text attribute)
          scope.select2tags = _(scope.tags).map(function(arg) {
            return {'id': arg, 'text': arg};
          }).value();

          scope.getTags = function () {
            $log.log('getTags');
            return {
              tags: function() {
                $log.log('tags');
                return null;
              },
              tokenSeparators: [',', ' ']
            };
          };
          scope.$watch('select2tags', function(newValue, oldValue) {
            function add(tag) {
              $log.log('newtag: ' + tag + ' for doc: ' + scope.id);
              scope.$emit('document:addtag', {'id': scope.id, 'tag': tag});
            }
            // This is the ng-click handler to remove an item
            function remove(tag) {
              scope.$emit('document:removetag', {'id': scope.id, 'tag': tag});
            }

            if (newValue !== undefined && oldValue!== undefined && newValue !== oldValue) {
              var tag;
              $log.log('change tags: ' + scope.tags + ' - newValue: ' + JSON.stringify(newValue) + ' - oldValue: ' + JSON.stringify(oldValue));
              if (newValue.length === 0 && oldValue.length > 0) {
                tag = _.first(oldValue);
                $log.log('remove tag ' + tag.id);
                remove(tag.id);
              } else if (newValue.length > oldValue.length) {
                tag = _.first(_.difference(newValue, oldValue));
                $log.log('add tag ' + tag.id);
                add(tag.id);
              } else if (newValue.length < oldValue.length) {
                tag = _.first(_.difference(oldValue, newValue));
                $log.log('remove tag ' + tag.id);
                remove(tag.id);
              }
            }
          });
        },
        post : function(scope, element) {
          var template = '<input ui-select2="getTags()" ng-change="change()" ng-model="select2tags" style="width: 200px;" />';
          element.replaceWith($compile(template)(scope));
        }
      };
    }
  };
}]);
