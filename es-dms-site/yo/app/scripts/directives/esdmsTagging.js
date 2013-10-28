'use strict';

esDmsSiteApp.directive('esdmsTagging', ['$log', '$compile', function ($log, $compile) {
	$log.log('Start esdmsTagging');
  return {
    restrict: 'E',
    scope: { id: '=', tags: '='},
    compile: function() {
      return {
        pre: function(scope) {
          
          scope.$watch('tags', function(newValue, oldValue) {
            function add(tag) {
              scope.$emit('document:addtag', {'id': scope.id, 'tag': tag});
            }
            function remove(tag) {
              scope.$emit('document:removetag', {'id': scope.id, 'tag': tag});
            }

            if (newValue !== undefined && oldValue!== undefined && newValue !== oldValue) {
              var tag;
              $log.log('tags-input has been updated - newValue: ' + newValue + ' - oldValue: ' + oldValue);
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
          var template = '<tags-input ng-model="tags" ng-class="esdms-tag"></tags-input>';
          element.replaceWith($compile(template)(scope));
        }
      };
    }
  };
}]);

esDmsSiteApp.directive('esdmsTagging2', ['$log', '$compile', 'searchService', function ($log, $compile, searchService) {
  $log.log('Start esdmsTagging2');
  return {
    restrict: 'E',
    // template: '<input type="text" ng-model="selected" typeahead="state for state in states | filter:$viewValue | limitTo:8">',
    // template: '<input type="text" ng-model="selected" typeahead="tag for tag in suggest($viewValue)" typeahead-on-select="tagSelected()">',
    template: '<tags-input ng-model="tags" ng-class="esdms-tag"></tags-input>',
    scope: { id: '=', tags: '='},
    compile: function() {
      return {
        pre: function(scope) {
          scope.suggestedTags = [];
          scope.suggest = function(criteria) {
            searchService.suggestTags(criteria, function(result) {
              scope.suggestedTags.splice(0, scope.suggestedTags.length);
              _.each(result.terms, function(item) {
                scope.suggestedTags.push(item.term);
              });
              $log.log(JSON.stringify(scope.suggestedTags));
              // return scope.suggestedTags;
            });
            return scope.suggestedTags;
          };
          scope.selected = undefined;
          scope.tagSelected = function() {
            $log.log('Tag selected: ' + scope.selected);
          };
        }
        //   scope.select2options = {
        //     'simple_tags': true,
        //     'tags': [],
        //     'tokenSeparators': [',', ' ']
        //   };

        //   scope.$watch('tags', function(newValue, oldValue) {
        //     function add(tag) {
        //       $log.log('add tag: ' + tag + ' for doc: ' + scope.id);
        //       scope.$emit('document:addtag', {'id': scope.id, 'tag': tag});
        //     }
        //     // This is the ng-click handler to remove an item
        //     function remove(tag) {
        //       scope.$emit('document:removetag', {'id': scope.id, 'tag': tag});
        //     }

        //     if (newValue !== undefined && oldValue!== undefined && newValue !== oldValue) {
        //       var tag;
        //       $log.log('select2tags has been updated - newValue: ' + newValue + ' - oldValue: ' + oldValue);
        //       if (newValue.length === 0 && oldValue.length > 0) {
        //         tag = _.first(oldValue);
        //         $log.log('remove tag ' + tag);
        //         remove(tag);
        //       } else if (newValue.length > oldValue.length) {
        //         tag = _.first(_.difference(newValue, oldValue));
        //         $log.log('add tag ' + tag);
        //         add(tag);
        //       } else if (newValue.length < oldValue.length) {
        //         tag = _.first(_.difference(oldValue, newValue));
        //         $log.log('remove tag ' + tag);
        //         remove(tag);
        //       }
        //     }
        //   }, true);
        // },
        // post : function(scope, element) {
        //   var template = '<input ui-select2="select2options" ng-model="tags" />';
        //   element.replaceWith($compile(template)(scope));
        // }
      };
    }
  };
}]);
