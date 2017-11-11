var fuzzyhound = (function ($, FuzzySearch) {
    var fuzzyhound = new FuzzySearch({output_limit: 6, output_map:"alias"});
    var loaded = null;

    $.ajaxSetup({cache: true});
    function setSource(url, keys) {
        $.getJSON(url).then(function (response) {
            fuzzyhound.setOptions({
                source: response,
                keys: keys
            });
            if ($.isFunction(loaded))
                loaded(response);
        });
    }

    return {
        get: function () {
            return fuzzyhound;
        },
        setSource: setSource,
        onLoad: function (callback) {
            loaded = callback;
        }
    }
}(jQuery, FuzzySearch));