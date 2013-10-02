'use strict';

angular.module('esDmsSiteApp')
  .controller('DocumentsRatingCtrl', function ($log, $scope, sharedService, ratingService) {
    $scope.rating = { score: 0, overStar: false };

    $scope.getRating = function(document) {
      $log.log('getRating: ' + document.id + ' - ' + sharedService.getCurrentUser().id);
      _.find(document.ratings, function(rating) {
        if (rating.user === sharedService.getCurrentUser().id) {
          $scope.rating.score = rating.score;
          return false;
        }
      });
    };
    $scope.hoveringRating = function(score) {
      $scope.rating.score = score;
      $scope.rating.overStar = true;
    };
  
    $scope.addRating = function(id) {
        $log.log('Add rating - ' + $scope.rating.overStar + ' - ' + id + ' - ' + $scope.rating.score);
      if ($scope.rating.overStar === true) {
        $log.log('Add rating - ' + id + ' - ' + $scope.rating.score);
        ratingService.create(id, $scope.rating.score);
        $scope.rating.overStar = false;
      }
    };
  });
