import { formatDate } from "../app/date-utils";
import {addSearchColumns, searchDelay} from "./utils";

function showCourse(data) {
    const modal = $('#floatedCourseModal');
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
    modal.find('#type_icon').text(data['course_type'] === 'Theory' ? 'class' : 'opacity');
    modal.find('#link').attr('href', '/admin/dean/floated/' + data['course_code'] + '/attendance.csv');
    modal.find('#register').attr('href', '/admin/dean/floated/' + data['course_code']);

    if (data['createdAt'] && data['createdAt'] !== '')
        modal.find('#floated-at').html(formatDate(new Date(data['createdAt'])));
    else
        modal.find('#floated-at').html('No Record');

    if (data['createdBy'] && data['createdBy'] !== '')
        modal.find('#floated-by').html(data['createdBy']);
    else
        modal.find('#floated-by').html('No Record');
}

const header = $("meta[name='_csrf_header']").attr("content");
const token = $("meta[name='_csrf']").attr("content");

const floatedCourseTable = $('#floatedCourseTable');

const table = floatedCourseTable.DataTable({
    scrollY: true,
    scrollCollapse: true,
    'ajax': {
        'contentType': 'application/json',
        'url': '/admin/dean/api/floated',
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
    dom: 'lBfrtip',
    buttons: [
        'copy', 'csv', 'print'
    ],
    "lengthMenu": [[10, 25, 50, 100, -1], [10, 25, 50, 100, "All"]],
    "initComplete": function () {
        searchDelay(table);
        addSearchColumns(table);
    }
});

floatedCourseTable.find('tbody').on( 'click', 'tr', function () {
    showCourse(table.row(this).data());
});