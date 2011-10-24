angular.filter('newlines', function(text) {
    return text.replace(/\n/g, '<br>');
});

angular.filter.blackZero = function(n){
    if (n === 0) return "<span class='black'>0</span>";
    return ""+n;
};

angular.filter.hideZeroAndParens = function(n){
    if(n === 0) return "";
    return '(' + n + ')'
};

angular.filter.truncate = function(str){
    return FTA.truncateElementText(this.$element[0], str);
};

angular.filter.truncateTo80 = function(str){
    return FTA.truncateElementText(this.$element[0], str, 80);
};

angular.filter.floor = function(n){
    return Math.floor(n);
};

angular.filter.colorByTranche = function(n){
    if (n < 100) return "<span class='passed-false'>" + n + "</span>";
    return "<span class='passed-true'>" + n + "</span>";
};