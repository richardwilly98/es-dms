'use strict';

/** utils service */
esDmsSiteApp.service('wordcloud', function (d3) {

    var WordCloud = function (w, h, maxEntries, addSearch, elem) {

        var me = {},
            fill = d3.scale.category10(),
            words = [],
            max,
            scale = 1,
            complete = 0,
            tags,
            fontSize,
            cloud,
            svg,
            background,
            transitionDuration = 700,
            vis;

        function draw(data, bounds) {
            scale = bounds ? Math.min(
                w / Math.abs(bounds[1].x - w / 2),
                w / Math.abs(bounds[0].x - w / 2),
                h / Math.abs(bounds[1].y - h / 2),
                h / Math.abs(bounds[0].y - h / 2)) / 2 : 1;
            words = data;
            var text = vis.selectAll("text")
                .data(words, function(d) { return d.text.toLowerCase(); });
            text.transition()
                .duration(transitionDuration)
                .attr("transform", function(d) { return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")"; })
                .style("font-size", function(d) { return d.size + "px"; });
            text.enter().append("text")
                .attr("text-anchor", "middle")
                .attr("transform", function(d) { return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")"; })
                .style("font-size", function(d) { return d.size + "px"; })
                .on("click",function (d) {
                    var tag;
                    tag = d.text.replace('#', '');
                    addSearch(tag);
                })
                .on("mouseover", function (d) {
                    d3.select(this)
                        .style("cursor", "pointer")
                        .transition()
                        .duration(transitionDuration)
                        .style("font-size", function(d) { return (d.size * 1.5) + "px"; });
                })
                .on("mouseout", function (d) {
                    d3.select(this)
                        .style("cursor", "normal")
                        .transition()
                        .duration(transitionDuration)
                        .style("font-size", function(d) { return d.size + "px"; });
                })
                .style("opacity", 1e-6)
                .transition()
                .duration(transitionDuration)
                .style("opacity", 1);
            text.style("font-family", function(d) { return d.font; })
                .style("fill", function(d) { return fill(d.text.toLowerCase()); })
                .text(function(d) { return d.text; });
            var exitGroup = background.append("g")
                .attr("transform", vis.attr("transform"));
            var exitGroupNode = exitGroup.node();
            text.exit().each(function() { exitGroupNode.appendChild(this); });
            exitGroup.transition()
                .duration(transitionDuration)
                .style("opacity", 1e-6)
                .remove();
            vis.transition()
                .delay(100)
                .duration(transitionDuration)
                .attr("transform", "translate(" + [w >> 1, h >> 1] + ")scale(" + scale + ")");
        }

        me.init = function() {
            cloud = d3.layout.cloud()
                .timeInterval(1)
                .size([w, h])
                .fontSize(function(d) { return fontSize(+d.value); })
                .text(function(d) { return d.key; })
                .on("end", draw);

            svg = d3.select("#" + elem.context.id).append("svg").attr("width", w).attr("height", h);

            background = svg.append("g");
            vis = svg.append("g").attr("transform", "translate(" + [w >> 1, h >> 1] + ")");
        };

        function generate() {
            cloud.font("Impact").spiral("archimedean");
            fontSize = d3.scale.log().range([10, 85]);

            if (tags.length) fontSize.domain([+tags[tags.length - 1].value || 1, +tags[0].value]);
            complete = 0;
            words = [];
            cloud.stop().words(tags.slice(0, max = Math.min(tags.length, maxEntries))).start();
        }

        me.redraw = function(dataSource) {
            tags = dataSource;
            generate();
        };

        me.init();
        
        return me;
    };
      
    return { WordCloud: WordCloud };
});

esDmsSiteApp.controller('WorldCloudCtrl', function ($log, $scope, searchService) {
  $scope.model = {};
  $scope.selectedWord = '';
  // $scope.criteria = '';
  $scope.live = true;
  $scope.cloud = {words: [
    {'key': 'Loading', 'value': 1}
  ]};

  $scope.init = function() {
    $log.log('init');
    var words = [];
    searchService.suggestTags('*', 50, function(data) {
      _.each(data.terms, function(term) {
        $log.log('Add word ' + term.term + ' - count: ' + term.count);
        words.push({'key': term.term, 'value': term.count});
      });
      $scope.cloud.words = words;
    });
  };

  $scope.addSearchString = function (word) {
    $log.log('*** addSearchString : ' + word + ' ***');
    $scope.selectedWord = ' - ' + word;
    $scope.model.criteria = 'tags: ' + word;
    $scope.$apply();
  }

  $scope.init();

});
