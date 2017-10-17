var fuzzyhound = (function () {
    var fuzzyhound = new FuzzySearch({output_limit: 6, output_map:"alias"});

    $.ajaxSetup({cache: true});
    function setSource(url, keys) {
        $.getJSON(url).then(function (response) {
            fuzzyhound.setOptions({
                source: response,
                keys: keys
            })
        });
    }

    return {
        get: function () {
            return fuzzyhound;
        },
        setSource: setSource
    }
})();