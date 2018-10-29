import { loadImage } from '../app/utils'
import { attachSelectors, fixDate, searchDelay } from './utils'

function showFaculty(data) {
    const modal = $('#facultyModal');
    modal.modal();

    modal.find('#name').html(data['user_name']);
    modal.find('#faculty_id').html(data['facultyId']);
    modal.find('#designation').html(data['designation']);
    modal.find('#department').html(data['user_department_name']);
    modal.find('#working').html(data['working'] ? 'Working' : 'Inactive');
    modal.find('#working').attr('class', 'capsule p-small text-white ' + (data['working'] ? 'bg-success' : 'bg-danger'));
    modal.find('#link').attr('href', '/admin/dean/faculty/' + data['facultyId']);

    modal.find('#avatar').attr('src', '/img/account.svg');
    if ((data['avatar-url'] && data['avatar-url'] !== '') || (data['original-avatar-url'] && data['original-avatar-url'] !== '')) {
        let avatar = data['original-avatar-url'];
        if (avatar === '')
            avatar = data['avatar-url'];

        loadImage(avatar)
            .then(function () {
                modal.find('#avatar').attr('src', avatar);
            });
    }

    if (data['user_gender'] && data['user_gender'] !== '') {
        const genderSpan = modal.find('#gender');
        const gender = data['user_gender'];
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
        modal.find('#registered-at').html(moment(fixDate(data['createdAt'])).format('dddd, MMMM Do YYYY, h:mm:ss a'));
    else
        modal.find('#registered-at').html('No Record');

    if (data['createdBy'] && data['createdBy'] !== '')
        modal.find('#registered-by').html(data['createdBy']);
    else
        modal.find('#registered-by').html('No Record');
}

const header = $("meta[name='_csrf_header']").attr("content");
const token = $("meta[name='_csrf']").attr("content");

const facultyTable = $('#facultyTable');

const table = facultyTable.DataTable({
    scrollY: true,
    scrollCollapse: true,
    'ajax': {
        'contentType': 'application/json',
        'url': '/admin/dean/api/faculty',
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
        defaultContent: '/img/account.svg',
        render: function (data) {
            if (data && data !== '')
                return '<img class="rounded-circle" src="' + data + '" height="48px" />';
            return '<img class="rounded-circle" src="/img/account.svg" />';
        }
    }, {
        data: 'facultyId'
    }, {
        data: 'user_name'
    }, {
        data: 'user_gender',
        name: 'gender'
    }, {
        data: 'designation'
    }, {
        data: 'user_department_name'
    }, {
        data: 'working',
        name: 'working',
        render: function (data) {
            let text = 'Working';
            let css = 'bg-success';
            if (!data) {
                text = 'Inactive';
                css = 'bg-danger';
            }

            return '<span class="capsule p-small text-white ' + css + '">' + text + '</span>';
        }
    }, {
        data: 'user_email'
    }],
    dom: 'lBfrtip',
    buttons: ['copy', 'csv', 'excel', 'pdf', 'print'],
    "initComplete": function () {
        searchDelay(table);
        attachSelectors(table, 'DataTables_facultyTable_/admin/dean/faculty', [{
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
});