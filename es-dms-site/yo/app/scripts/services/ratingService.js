'use strict';

angular.module('esDmsSiteApp')
  .service('ratingService', ['$log', '$resource', function RatingService($log, $resource) {
    var resource = $resource('api/ratings/:id/:action', {id:'@id'}, {
      getRatings: {method:'GET', params: {action: '_all'}},
      getTotalRatings: {method:'GET', params: {action: '_total'}},
      getAverageRatings: {method:'GET', params: {action: '_average'}}
    });
    return {
      create: function(id, score) {
        $log.info('creating rating: ' + id + ' - ' + score);
        var ratingRequest = {'itemId': id, 'score': score};
        var rating = new resource.save(ratingRequest);
        $log.log('saved rating: ' + rating);
      }
    };
  }]);
