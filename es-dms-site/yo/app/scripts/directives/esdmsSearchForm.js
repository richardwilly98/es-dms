'use strict';

angular.module('esDmsSiteApp')
  .directive('esdmsSearchForm', function ($log) {
    return {
      templateUrl: 'views/search/search-form.html',
      scope: {
        parentModel: '=ngModel'
      },
      restrict: 'E',
      link: function postLink(scope) {
        scope.model = {
          action: 'icon-search',
          selected: 'simple'
        };
  
        scope.attributes = [
          'attributes.author:',
          'attributes.creation:',
          'attributes.lockedBy:',
          'attributes.modified:',
          'attributes.status:',
          'name:',
          'ratings.date:',
          'ratings.score:',
          'ratings.user:',
          'tags:'
        ];
        scope.change = function (menu) {
          scope.model.selected = menu;
          if (menu === 'simple') {
            scope.model.action = 'icon-search';
            return;
          }
          if (menu === 'metadata') {
            scope.model.action = 'icon-lightbulb';
            return;
          }
        };
  
        scope.setCriteria = function () {
          $log.log('setCriteria: ' + scope.model.criteria);
          scope.parentModel.criteria = scope.model.criteria;
        };
      }
    };
  });
