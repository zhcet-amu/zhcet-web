(function () {
    function fixDate(date) {
        return date.split('[')[0];
    }

    function showCourse(data) {
        var modal = $('#floatedCourseModal');
        modal.modal();

        modal.find('#title').html(data['course_title']);
        modal.find('#code').html(data['course_code']);
        modal.find('#department').html(data['course_department_name']);
        modal.find('#type').html(data['course_type']);
        modal.find('#description').html(data['course_description']);
        modal.find('#category').html(data['course_category']);
        modal.find('#branch').html(data['course_branch']);
        modal.find('#semester').html(data['course_semester']);
        modal.find('#credits').html(data['course_credits']);
        modal.find('#num').html(data['num_students']);
        modal.find('#sections').html(data['sections']);
        modal.find('#type_icon').attr('class', (data['course_type'] === 'Theory' ? 'icon-book' : 'icon-lab'));
        modal.find('#link').attr('href', '/dean/floated/' + data['course_code'] + '/attendance/download');

        if (data['createdAt'] && data['createdAt'] !== '')
            modal.find('#floated-at').html(moment(fixDate(data['createdAt'])).format('dddd, MMMM Do YYYY, h:mm:ss a'));
        else
            modal.find('#floated-at').html('No Record');

        if (data['createdBy'] && data['createdBy'] !== '')
            modal.find('#floated-by').html(data['createdBy']);
        else
            modal.find('#floated-by').html('No Record');
    }

    $(document).ready(function () {
        var header = $("meta[name='_csrf_header']").attr("content");
        var token = $("meta[name='_csrf']").attr("content");

        var floatedCourseTable = $('#floatedCourseTable');

        var table = floatedCourseTable.DataTable({
            scrollY:        true,
            scrollCollapse: true,
            'ajax': {
                'contentType': 'application/json',
                'url': '/dean/api/floated',
                'type': 'POST',
                'data': function (d) {
                    return JSON.stringify(d);
                },
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(header, token);
                }
            },
            "deferRender": true,
            "processing": true,
            "serverSide": true,
            searchDelay: 750,
            columns: [{
                data: 'course_code'
            }, {
                data: 'course_title'
            }, {
                data: 'course_department_name'
            }, {
                data: 'num_students',
                searchable: false,
                orderable: false
            }, {
                data: 'course_semester'
            }, {
                data: 'course_credits'
            }, {
                data: 'course_category'
            }, {
                data: 'course_branch'
            }, {
                data: 'course_type'
            }],
            "initComplete": function () {
                var $searchInput = $('div.dataTables_filter input');

                $searchInput.unbind();

                $searchInput.bind('keyup', $.debounce(1000, function(e) {
                    table.search(this.value).draw();
                }));
            }
        });

        floatedCourseTable.find('tbody').on( 'click', 'tr', function () {
            showCourse(table.row(this).data());
        } );
    });
})();