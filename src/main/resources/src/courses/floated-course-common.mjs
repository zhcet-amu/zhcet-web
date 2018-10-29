(function () {
    const deleteModal = $('#remove-student-modal');
    $('#registration-modal').modal('show');
    $('#registrationTable').dataTable({
        scrollY:        true,
        scrollCollapse: true,
        "order": [],
        dom: 'lBfrtip',
        buttons: ['copy', 'csv', 'excel', 'pdf', 'print']
    });

    $('#confirmRegistrationTable').dataTable({
        scrollY:        true,
        scrollCollapse: true,
        "order": []
    });

    $('.remove-student').on('click', function (element) {
        const student = $(element.target);
        const enrolment = student.attr('data-enrolment');

        deleteModal.find('#remove-student-id').text(enrolment);
        const deleteInput = deleteModal.find('#student-to-delete');
        deleteInput.val(enrolment);

        deleteModal.modal();
    });

}());