'use strict';

angular.module('esDmsSiteApp')
  .directive('esdmsPreview', ['$log', function ($log) {
    return {
      template: '<div class="dropdown">' +
        '<a class="dropdown-toggle label"><i class="icon-white icon-picture"></i>&nbsp;Preview</a>' +
        '<div class="dropdown-menu" data-ng-include="\'views/documents/preview.html\'"></div>' +
        '</div>',
      restrict: 'E',
      link: function (scope, element) {
        var toggle = element.find('.dropdown-toggle');
        toggle.bind('click', function() {
          // TODO: should be provided by directive attributes.
          $log.log('click preview');
          scope.$apply('preview(document.id)');
        });
      }
    };
  }]);
