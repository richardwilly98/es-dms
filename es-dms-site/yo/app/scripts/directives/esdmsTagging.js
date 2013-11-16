'use strict';

esDmsSiteApp.directive('esdmsTagging', ['$log', '$compile', '$q', 'searchService', function ($log, $compile, $q, searchService) {
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

          scope.loadItems = function(text) {
            $log.log('loadItems: '+ text);
            var deferred = $q.defer();
            var suggestedTags = [];
            searchService.suggestTags(text, 10, function(result) {
              _.each(result.terms, function(item) {
                suggestedTags.push(item.term);
              });
              deferred.resolve(suggestedTags);
            });
            return deferred.promise;
          };
        },
        post : function(scope, element) {
          var template = '<tags-input ng-model="tags" ng-class="esdms-tag"><autocomplete source="loadItems"></autocomplete></tags-input>';
          // var template = '<tags-input ng-model="tags" ng-class="esdms-tag"></tags-input>';
          element.replaceWith($compile(template)(scope));
        }
      };
    }
  };
}]);
