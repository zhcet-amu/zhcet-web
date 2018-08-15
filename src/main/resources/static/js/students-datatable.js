(function ($) {
    function showStudent(data) {
        var modal = $('#studentModal');
        modal.modal();

        modal.find('#name').html(data['user_name']);
        modal.find('#faculty_no').html(data['facultyNumber']);
        modal.find('#enrolment_no').html(data['enrolmentNumber']);
        modal.find('#department').html(data['user_department_name']);
        modal.find('#link').attr('href', '/admin/dean/students/' + data['enrolmentNumber']);

        modal.find('#avatar').attr('src', '/img/account.svg');
        if ((data['avatar-url'] && data['avatar-url'] !== '') || (data['original-avatar-url'] && data['original-avatar-url'] !== '')) {
            var avatar = data['original-avatar-url'];
            if (avatar === '')
                avatar = data['avatar-url'];

            loadImage(avatar)
                .then(function () {
                    modal.find('#avatar').attr('src', avatar);
                });
        }

        if (data['user_details_gender'] && data['user_details_gender'] !== '') {
            var genderSpan = modal.find('#gender');
            var gender = data['user_details_gender'];
            genderSpan.html(gender);
            genderSpan.attr('class', 'capsule p-small ' + (gender === 'Male' ? 'blue' : 'pink') + '-dark');
        } else {
            modal.find('#gender-container').hide();
        }

        if (data['user_email'] && data['user_email'] !== '')
            modal.find('#email').html(data['user_email']);
        else
            modal.find('#email').html('No Email Registered');

        if (data['is-verified'])
            modal.find('#verified i').text('check');
        else
            modal.find('#verified i').text('');

        if (data['createdAt'] && data['createdAt'] !== '')
            modal.find('#registered-at').html(moment(DataUtils.fixDate(data['createdAt'])).format('dddd, MMMM Do YYYY, h:mm:ss a'));
        else
            modal.find('#registered-at').html('No Record');

        if (data['createdBy'] && data['createdBy'] !== '')
            modal.find('#registered-by').html(data['createdBy']);
        else
            modal.find('#registered-by').html('No Record');
    }

    function enableButton(table) {
        return function () {
            var selectedRows = table.rows( { selected: true } ).count();
            table.button( 2 ).enable( selectedRows > 0 );
        }
    }

    function changeStudent(table) {
        var data = table.rows( { selected: true } ).data();

        if (data.count() <= 0) {
            toastr.error('No student(s) selected');
            return;
        }

        // Setting enrolment numbers to be changed
        var enrolments = $('.enrolments');
        enrolments.html(''); // Clear previous values
        for (var i = 0; i < data.count(); i++)
            enrolments.append('<input name="enrolments" value="' + data[i].enrolmentNumber + '" />');

        // Set the student count
        $('.count').html(data.count());
        $('#section-modal').modal('show');
    }

    /* main */ (function () {
        var header = $("meta[name='_csrf_header']").attr("content");
        var token = $("meta[name='_csrf']").attr("content");

        var studentTable = $('#studentTable');

        var table = studentTable.DataTable({
            scrollY:        true,
            scrollCollapse: true,
            'ajax': {
                'contentType': 'application/json',
                'url': '/admin/dean/api/students',
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
            columnDefs: [{
                orderable: false,
                className: "select-checkbox",
                targets: 0
            }],
            columns: [{
                defaultContent: '',
                searchable: false,
                orderable: false
            },
                {
                    data: 'avatar-url',
                    searchable: false,
                    orderable: false,
                    defaultContent: '/img/account.svg',
                    render: function (data) {
                        if (data && data !== '')
                            return '<img class="rounded-circle" style="background-color: white" src="' + data + '" height="48px" />';
                        return '<img class="rounded-circle" style="background-color: white" src="/img/account.svg" />';
                    }
                }, {
                    data: 'facultyNumber'
                }, {
                    data: 'enrolmentNumber'
                }, {
                    data: 'user_name'
                }, {
                    data: 'user_details_gender',
                    name: 'gender'
                }, {
                    data: 'user_department_name'
                }, {
                    data: 'hallCode'
                }, {
                    data: 'section'
                }, {
                    data: 'status',
                    name: 'status'
                }, {
                    data: 'user_email'
                }],
            dom: 'lBfrtip',
            rowId: 'enrolmentNumber',
            stateSave: true,
            select: {
                style: 'os',
                selector: 'td:first-child'
            },
            buttons: [
                'selectAll',
                'selectNone',
                {
                    enabled: false,
                    text: 'Change Section/Status',
                    action: function () {
                        changeStudent(table)
                    }
                },
                'copy', 'csv', 'excel', 'pdf', 'print'
            ],
            "lengthMenu": [[10, 25, 50, 100, 200, 500], [10, 25, 50, 100, 200, 500]],
            "initComplete": function () {
                DataUtils.searchDelay(table);
                DataUtils.attachSelectors(table, 'DataTables_studentTable_/admin/dean/students', [{
                    id: '#stat',
                    defaultVal: 'A',
                    columnName: 'status'
                }, {
                    id: '#gend',
                    columnName: 'gender'
                }]);
            }
        });

        table.on('select', enableButton(table));
        table.on('deselect', enableButton(table));

        studentTable.find('tbody').on( 'click', 'tr', function (el) {
            if ($(el.target).is('.select-checkbox'))
                return;
            showStudent(table.row(this).data());
        } );
    })();
}(jQuery));