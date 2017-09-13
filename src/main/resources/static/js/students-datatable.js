function showStudent(data) {
    console.log(data);
    var modal = $('#studentModal');
    modal.modal();

    modal.find('#name').html(data['name']);
    modal.find('#faculty_no').html(data['faculty-number']);
    modal.find('#enrolment_no').html(data['enrolment-number']);
    modal.find('#department').html(data['department']);
    modal.find('#link').attr('href', '/dean/students/' + data['enrolment-number']);

    if (data['avatar-url'] && data['avatar-url'] !== '')
        modal.find('#avatar').attr('src', data['avatar-url']);
    else
        modal.find('#avatar').attr('src', 'https://zhcet-web-amu.firebaseapp.com/static/img/account.svg');

    if (data['email'] && data['email'] !== '')
        modal.find('#email').html(data['email']);
    else
        modal.find('#email').html('No Email Registered');

    if (data['is-verified'])
        modal.find('#verified i').addClass('icon-check2');
    else
        modal.find('#verified i').removeClass('icon-check2');

    if (data['created-at'] && data['created-at'] !== '')
        modal.find('#registered-at').html(moment(data['created-at']).format('dddd, MMMM Do YYYY, h:mm:ss a'));
    else
        modal.find('#registered-at').html('No Record');

    if (data['created-by'] && data['created-by'] !== '')
        modal.find('#registered-by').html(data['created-by']);
    else
        modal.find('#registered-by').html('No Record');
}

$(document).ready(function () {
    var header = $("meta[name='_csrf_header']").attr("content");
    var token = $("meta[name='_csrf']").attr("content");

    var studentTable = $('#studentTable');

    var table = studentTable.DataTable({
        scrollY:        true,
        scrollCollapse: true,
        'ajax': {
            'contentType': 'application/json',
            'url': '/dean/students',
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
            data: 'avatar-url',
            searchable: false,
            orderable: false,
            defaultContent: 'https://storage.googleapis.com/material-icons/external-assets/v4/icons/svg/ic_account_circle_black_48px.svg',
            render: function (data, type, row) {
                if (data && data !== '')
                    return '<img class="rounded-circle" src="' + data + '" height="48px" />';
                return '<img class="rounded-circle" src="https://zhcet-web-amu.firebaseapp.com/static/img/account.svg" />';
            }
        }, {
            data: 'faculty-number'
        }, {
            data: 'enrolment-number'
        }, {
            data: 'name'
        }, {
            data: 'department'
        }, {
            data: 'hall-code'
        }, {
            data: 'section'
        }, {
            data: 'status'
        }, {
            data: 'email'
        }],
        "initComplete": function () {
            var $searchInput = $('div.dataTables_filter input');

            $searchInput.unbind();

            $searchInput.bind('keyup', $.debounce(1000, function(e) {
                table.search(this.value).draw();
            }));
        }
    });

    studentTable.find('tbody').on( 'click', 'tr', function () {
        showStudent(table.row(this).data());
    } );
});