(function () {
    function fixDate(date) {
        return date.split('[')[0];
    }

    function showFaculty(data) {
        var modal = $('#facultyModal');
        modal.modal();

        modal.find('#name').html(data['user_name']);
        modal.find('#faculty_id').html(data['facultyId']);
        modal.find('#designation').html(data['designation']);
        modal.find('#department').html(data['user_department_name']);
        modal.find('#working').html(data['working'] ? 'Active' : 'Inactive');
        modal.find('#working').attr('class', 'capsule ' + (data['working'] ? 'bg-success' : 'bg-danger'));
        modal.find('#link').attr('href', '/dean/faculty/' + data['facultyId']);

        if (data['avatar-url'] && data['avatar-url'] !== '')
            modal.find('#avatar').attr('src', data['avatar-url']);
        else
            modal.find('#avatar').attr('src', 'https://zhcet-web-amu.firebaseapp.com/static/img/account.svg');

        if (data['user_email'] && data['user_email'] !== '')
            modal.find('#email').html(data['user_email']);
        else
            modal.find('#email').html('No Email Registered');

        if (data['is-verified'])
            modal.find('#verified i').addClass('icon-check2');
        else
            modal.find('#verified i').removeClass('icon-check2');

        if (data['createdAt'] && data['createdAt'] !== '')
            modal.find('#registered-at').html(moment(fixDate(data['createdAt'])).format('dddd, MMMM Do YYYY, h:mm:ss a'));
        else
            modal.find('#registered-at').html('No Record');

        if (data['createdBy'] && data['createdBy'] !== '')
            modal.find('#registered-by').html(data['createdBy']);
        else
            modal.find('#registered-by').html('No Record');
    }

    $(document).ready(function () {
        var header = $("meta[name='_csrf_header']").attr("content");
        var token = $("meta[name='_csrf']").attr("content");

        var facultyTable = $('#facultyTable');

        var table = facultyTable.DataTable({
            scrollY:        true,
            scrollCollapse: true,
            'ajax': {
                'contentType': 'application/json',
                'url': '/dean/api/faculty',
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
            stateSave: true,
            searchDelay: 750,
            columns: [{
                data: 'avatar-url',
                searchable: false,
                orderable: false,
                defaultContent: 'https://zhcet-web-amu.firebaseapp.com/static/img/account.svg',
                render: function (data, type, row) {
                    if (data && data !== '')
                        return '<img class="rounded-circle" src="' + data + '" height="48px" />';
                    return '<img class="rounded-circle" src="https://zhcet-web-amu.firebaseapp.com/static/img/account.svg" />';
                }
            }, {
                data: 'facultyId'
            }, {
                data: 'user_name'
            }, {
                data: 'designation'
            }, {
                data: 'user_department_name'
            }, {
                data: 'working'
            }, {
                data: 'user_email'
            }],
            "initComplete": function () {
                var $searchInput = $('div.dataTables_filter input');

                $searchInput.unbind();

                $searchInput.bind('keyup', $.debounce(1000, function(e) {
                    table.search(this.value).draw();
                }));
            }
        });

        facultyTable.find('tbody').on( 'click', 'tr', function () {
            showFaculty(table.row(this).data());
        } );
    });
})();