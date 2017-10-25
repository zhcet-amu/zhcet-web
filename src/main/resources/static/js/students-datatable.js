(function () {
    function fixDate(date) {
        return date.split('[')[0];
    }

    function showStudent(data) {
        var modal = $('#studentModal');
        modal.modal();

        modal.find('#name').html(data['user_name']);
        modal.find('#faculty_no').html(data['facultyNumber']);
        modal.find('#enrolment_no').html(data['enrolmentNumber']);
        modal.find('#department').html(data['user_department_name']);
        modal.find('#link').attr('href', '/dean/students/' + data['enrolmentNumber']);

        if (data['avatar-url'] && data['original-avatar-url'] !== '')
            modal.find('#avatar').attr('src', data['original-avatar-url']);
        else
            modal.find('#avatar').attr('src', 'https://zhcet-backend.firebaseapp.com/static/img/account.svg');

        if (data['user_details_gender'] && data['user_details_gender'] !== '') {
            var genderSpan = modal.find('#gender');
            var gender = data['user_details_gender'];
            genderSpan.html(gender);
            genderSpan.attr('class', 'capsule ' + (gender === 'Male' ? 'blue' : 'pink') + '-dark');
        } else {
            modal.find('#gender-container').hide();
        }

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

    function setSelectInput(context, selected) {
        var values = ['A', 'G', 'N'];
        var statuses = ['Active : A', 'Graduated : G', 'Inactive : N'];

        context.api().columns(8).every( function () {
            var column = this;
            var select = $('<select id="stat"><option value="">All</option></select>')
                .appendTo( $('#statuses') )
                .on( 'change', function () {
                    var val = $.fn.dataTable.util.escapeRegex(
                        $(this).val()
                    );

                    if (val !== column.search()) {
                        column.search(val).draw();
                    }
                } );

            $.each(values, function ( d, j ) {
                select.append( '<option value="'+j+'">'+statuses[d]+'</option>' )
            } );
        } );

        if (selected === '')
            return;

        $('#statuses').find('option[value=' + selected + ']').prop('selected', true);
    }

    function enableButton(table) {
        return function () {
            var selectedRows = table.rows( { selected: true } ).count();
            table.button( 2 ).enable( selectedRows > 0 );
        }
    }

    $(document).ready(function () {
        var header = $("meta[name='_csrf_header']").attr("content");
        var token = $("meta[name='_csrf']").attr("content");

        var key = 'DataTables_studentTable_/dean/students';
        var selected = 'A';
        if (key in localStorage) {
            var data = JSON.parse(localStorage.getItem(key));
            selected = data['columns'][8]['search'].search;
        }

        var studentTable = $('#studentTable');

        var table = studentTable.DataTable({
            scrollY:        true,
            scrollCollapse: true,
            'ajax': {
                'contentType': 'application/json',
                'url': '/dean/api/students',
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
                    defaultContent: 'https://zhcet-backend.firebaseapp.com/static/img/account.svg',
                    render: function (data, type, row) {
                        if (data && data !== '')
                            return '<img class="rounded-circle" style="background-color: white" src="' + data + '" height="48px" />';
                        return '<img class="rounded-circle" style="background-color: white" src="https://zhcet-backend.firebaseapp.com/static/img/account.svg" />';
                    }
                }, {
                    data: 'facultyNumber'
                }, {
                    data: 'enrolmentNumber'
                }, {
                    data: 'user_name'
                }, {
                    data: 'user_details_gender'
                }, {
                    data: 'user_department_name'
                }, {
                    data: 'hallCode'
                }, {
                    data: 'section'
                }, {
                    data: 'status'
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
                        var data = table.rows( { selected: true } ).data();

                        if (data.count() <= 0) {
                            toastr.error('No student(s) selected');
                            return;
                        }

                        // Setting enrolment numbers to be changed
                        var enrolments = $('.enrolments');
                        for (var i = 0; i < data.count(); i++)
                            enrolments.append('<input name="enrolments" value="' + data[i].enrolmentNumber + '" />');

                        // Set the student count
                        $('.count').html(data.count());
                        $('#section-modal').modal('show');
                    }
                }
            ],
            "initComplete": function () {
                var $searchInput = $('div.dataTables_filter input');

                $searchInput.unbind();
                $searchInput.bind('keyup', $.debounce(1000, function(e) {
                    table.search(this.value).draw();
                }));

                setSelectInput(this, selected);
            }
        });

        table.on('select', enableButton(table));
        table.on('deselect', enableButton(table));

        studentTable.find('tbody').on( 'click', 'tr', function (el) {
            if ($(el.target).is('.select-checkbox'))
                return;
            showStudent(table.row(this).data());
        } );
    });
}());