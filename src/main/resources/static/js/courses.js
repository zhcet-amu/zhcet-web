(function ($, List) {
    var showFloatedOnly = false;

    function filterFloated(list, filter) {
        if (filter) {
            list.filter(function (item) {
                return item.values().floated !== '';
            });
        } else {
            list.filter();
        }
    }

    var options = {
        valueNames: [ 'code', 'title', 'semester', 'floated' ],
        page: 10,
        pagination: true
    };

    /* main */ (function () {
        var courseList = new List('courses', options);
        $('#toggle-floated').click(function (event) {
            showFloatedOnly = !showFloatedOnly;
            filterFloated(courseList, showFloatedOnly);
            var btn = $(event.target);
            btn.toggleClass('btn-secondary');
            btn.toggleClass('btn-outline-success');
        });
    })();
}(jQuery, List));