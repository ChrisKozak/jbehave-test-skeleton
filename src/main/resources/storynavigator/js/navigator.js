
function MyController() {
    this.PARALLEL_RESULTS = 8;

    this.data = {};
    data = this.data;
    this.fetch('xref.json', this.data);

    // we fetch the data in all files (Parallel1/xref.json, Parallel2/xref.json, etc)
    for (var i = 1; i <= this.PARALLEL_RESULTS; i++){
        var parallelData = {};
        this.fetch('Parallel' + i + '/xref.json', parallelData);
        FTA.superMerge(true, this.data, parallelData);
    }

    // eliminate dups in the dropdown containing meta filters
    this.data.xref.metaTags = FTA.eliminateDuplicates(this.data.xref.metaTags).sort();

}

MyController.prototype = {
    filterStories: function(expression){ // we customize the filtering of stories to be able to AND the cross cuts
        var crosscut = expression['crosscut'];
        delete expression['crosscut'];

        var results = angular.Array.filter(this.data.xref.stories, expression);
        if (crosscut !== undefined){
            results = this.searchCrossCuts(results, [crosscut.one, crosscut.two, crosscut.three]);
        }

        expression.crosscut = crosscut; // restoring crosscut so that angular's reset behavior still works
        return results;
    },

    searchCrossCuts: function(arr, crossCutCriteria){
        return $.grep(arr, function(story){
            var matches = $(crossCutCriteria).map(function(idx, criterion){
                return story.meta.indexOf(criterion) >= 0;
            });

            return $.makeArray(matches).indexOf(false) < 0;
        });
    },

    successesFor: function(feature){
        var search = {meta:feature};
        if (feature === undefined) search = {};

        return this.totalScenariosFor(search, {passed:true});
    },

    failuresFor: function(feature){
        var search = {meta:feature};
        if (feature === undefined) search = {};

        return this.totalScenariosFor(search, {passed:false});
    },

    successRateFor: function(feature){
        var search = {meta:feature};
        if (feature === undefined) search = {};

        return this.successesFor(feature) *100 / this.totalScenariosFor(search);
    },

    totalScenariosFor: function(storySearch, scenarioSearch){
        if (storySearch === undefined){
            storySearch = "";
        }

        if (scenarioSearch === undefined){
            scenarioSearch = {};
        }

        var sum = 0;
        $.each(this.filterStories(storySearch), function(idx, story){
            sum += angular.Array.filter(story.scenarioResults, scenarioSearch).length;
        });
        return sum;
    },

    displayStringFor: function(feature){
        return feature.substring(8);
    },

    fetch: function(path, store){

        $.ajaxSetup({async:false}); // make this synchronous so we can populate the navigator with the full dataset right away

        $.get(path, function(response){
                $.extend(store, response);
        }, 'json');

    },

    showStoriesFor: function(feature){
        $("#tabs").tabs('select',1);
        this.search.crosscut.one = feature;
        this.filterStories(this.search);
    },

    showResults: function(story) {
        jQuery.FrameDialog.create({
            width: '80%',
            height: 700,
            url: story.html,
            title: 'Results for: ' + story.readableName,
            closeOnEscape: true,
            buttons: []
        })
    },

    showOccurrences: function(match) {
        var contents = $("<div>");
        var table = $("<table>");
        contents.append(table);

        table.append($("<thead><th>Story</th><th>Scenario</th><th>Outcome</th></thead>"));
        var self = this;

        $.each(match.occurrences, function(idx, elem){
            var story = angular.Array.filter(self.data.xref.stories, {path: elem.story})[0];
            var tr = $("<tr>");

            var storyTd = $("<td>");
            storyTd.text(story.readableName);
            storyTd.addClass("path passed-" + story.passed + " known-issue-" + story.hasKnownIssue);

            var scenarioTd = $("<td>");
            FTA.truncateElementText(scenarioTd, elem.scenario, 80);

            
            var outcomeTd = $("<td>");
            outcomeTd.text(elem.outcome);
            outcomeTd.addClass("passed-" + (elem.outcome === 'PASSED'));

            tr.append(storyTd);
            tr.append(scenarioTd);
            tr.append(outcomeTd);

            tr.click(function(){
                self.showResults(story);
            });

            table.append(tr);
        });

        contents.dialog({width: '80%', title: match.type + " " + match.annotatedPattern});
    },

    humanReadableDuration: function(ms){
      var seconds = Math.round(ms / 1000);
      var minutes = Math.floor(seconds/60);
      var seconds = seconds % 60;

      var str = "";
      if (minutes > 0) str+= minutes + "m ";
      str+= seconds + "s";
      return str;
    }

};

showScenarios = function(){
    $(".scenario-titles").show();
    $(".scenario-failures").show();
    $("#scenario-toggle").text('Hide scenario titles');
    $("#scenario-toggle").unbind('click');
    $("#scenario-toggle").click(hideScenarios);
    return false;
};

hideScenarios = function(){
    $(".scenario-titles").hide();
    $(".scenario-failures").hide();
    $("#scenario-toggle").text('Show scenario titles');
    $("#scenario-toggle").unbind('click');
    $("#scenario-toggle").click(showScenarios);
    return false;
};