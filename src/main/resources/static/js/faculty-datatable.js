(function ($) {
    function showFaculty(data) {
        var modal = $('#facultyModal');
        modal.modal();

        modal.find('#name').html(data['user_name']);
        modal.find('#faculty_id').html(data['facultyId']);
        modal.find('#designation').html(data['designation']);
        modal.find('#department').html(data['user_department_name']);
        modal.find('#working').html(data['working'] ? 'Working' : 'Inactive');
        modal.find('#working').attr('class', 'capsule p-small ' + (data['working'] ? 'bg-success' : 'bg-danger'));
        modal.find('#link').attr('href', '/dean/faculty/' + data['facultyId']);

        modal.find('#avatar').attr('src', 'https://zhcet-backend.firebaseapp.com/static/img/account.svg');
        if (data['avatar-url'] !== '' || data['original-avatar-url'] !== '') {
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
            modal.find('#verified i').addClass('icon-check2');
        else
            modal.find('#verified i').removeClass('icon-check2');

        if (data['createdAt'] && data['createdAt'] !== '')
            modal.find('#registered-at').html(moment(DataUtils.fixDate(data['createdAt'])).format('dddd, MMMM Do YYYY, h:mm:ss a'));
        else
            modal.find('#registered-at').html('No Record');

        if (data['createdBy'] && data['createdBy'] !== '')
            modal.find('#registered-by').html(data['createdBy']);
        else
            modal.find('#registered-by').html('No Record');
    }

    /* main */ (function () {
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
                defaultContent: 'https://zhcet-backend.firebaseapp.com/static/img/account.svg',
                render: function (data) {
                    if (data && data !== '')
                        return '<img class="rounded-circle" src="' + data + '" height="48px" />';
                    return '<img class="rounded-circle" src="https://zhcet-backend.firebaseapp.com/static/img/account.svg" />';
                }
            }, {
                data: 'facultyId'
            }, {
                data: 'user_name'
            }, {
                data: 'user_details_gender',
                name: 'gender'
            }, {
                data: 'designation'
            }, {
                data: 'user_department_name'
            }, {
                data: 'working',
                name: 'working',
                render: function (data) {
                    var text = 'Working';
                    var css = 'bg-success';
                    if (!data) {
                        text = 'Inactive';
                        css = 'bg-danger';
                    }

                    return '<span class="capsule p-small '+css+'">'+text+'</span>';
                }
            }, {
                data: 'user_email'
            }],
            dom: 'lBfrtip',
            buttons: ['copy', 'csv', 'excel', 'pdf', 'print'],
            "initComplete": function () {
                DataUtils.searchDelay(table);
                DataUtils.attachSelectors(table, 'DataTables_facultyTable_/dean/faculty', [{
                    id: '#working-status',
                    columnName: 'working'
                }, {
                    id: '#gend',
                    columnName: 'gender'
                }]);
            }
        });

        facultyTable.find('tbody').on( 'click', 'tr', function () {
            showFaculty(table.row(this).data());
        } );
    })() ;
}(jQuery));