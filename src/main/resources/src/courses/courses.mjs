function filterFloated(list, filter) {
    if (filter) {
        list.filter(function (item) {
            return item.values().floated !== '';
        });
    } else {
        list.filter();
    }
}

const options = {
    valueNames: ['code', 'title', 'semester', 'floated'],
    page: 10,
    pagination: true
};

const courseList = new List('courses', options);
let showFloatedOnly = false;
$('#toggle-floated').click(function (event) {
    showFloatedOnly = !showFloatedOnly;
    filterFloated(courseList, showFloatedOnly);
    const btn = $(event.target);
    btn.toggleClass('btn-secondary');
    btn.toggleClass('btn-outline-success');
});
