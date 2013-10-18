'use strict';

angular.module('esDmsSiteApp')
  .controller('DocumentsRatingCtrl', ['$log', '$scope', 'sharedService', 'ratingService',
    function ($log, $scope, sharedService, ratingService) {
    $scope.rating = { score: 0, overStar: false };
    $scope.id;

    $scope.getRating = function(document) {
      $scope.id = document.id;
      $log.log('getRating: ' + document.id + ' - ' + sharedService.getCurrentUser().id);
      _.find(document.ratings, function(rating) {
        if (rating.user === sharedService.getCurrentUser().id) {
          $log.log('Found rating: ' + rating.score);
          $scope.rating.score = rating.score;
          return false;
        }
      });
      $scope.$watch('rating.score', function(newValue, oldValue) {
        if (newValue !== 0) {
          addRating();
        }
      });
    };
  
    function addRating() {
      ratingService.create($scope.id, $scope.rating.score);
    };
  }]);
