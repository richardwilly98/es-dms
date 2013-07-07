'use strict';

esDmsSiteApp.directive('esdmsTagging', ['$log', function ($log) {
	$log.log('Start esdmsTagging');
  return {
    restrict: 'E',
    scope: { id: '=', tags: '=', newtag: '=' },
    template:
      '<div>' +
      '<div class="input-append">' +
      '<input class="span1" type="text" placeholder="New tag" ng-model="tag"></input> ' +
      '<a class="btn" ng-click="add()"><i class="icon-plus"></i></a>' +
      '</div>' +
      '<div class="input-append">' +
      '<button ng-repeat="(idx, tag) in tags" class="btn btn-info" ng-click="remove(idx)">{{tag}}</button>' +
      '</div>' +
      '</div>',
    link: function ( $scope, $element ) {
      // FIXME: this is lazy and error-prone
      var input = angular.element( $element.children()[1] );
      // This adds the new tag to the tags array
      $scope.add = function() {
        $log.log('newtag: ' + $scope.tag + ' for doc: ' + $scope.id);
        $scope.$emit('document:addtag', {'id': $scope.id, 'tag': $scope.tag});
        $scope.tag = '';
      };
      // This is the ng-click handler to remove an item
      $scope.remove = function ( idx ) {
        $scope.$emit('document:removetag', {'id': $scope.id, 'tag': idx});
      };
      // Capture all keypresses
      input.bind( 'keypress', function ( event ) {
        // But we only care when Enter was pressed
        if ( event.keyCode === 13 ) {
          // There's probably a better way to handle this...
          $scope.$apply( $scope.add );
        }
      });
    }
  };
}]);
