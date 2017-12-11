(function () {
    $(document).ready(function () {
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
            var student = $(element.target);
            var enrolment = student.attr('data-enrolment');

            deleteModal.find('#remove-student-id').text(enrolment);
            var deleteForm = deleteModal.find('#delete-link');
            var action = deleteForm.attr('action');
            deleteForm.attr('action', action + enrolment);

            deleteModal.modal();
        });

        var deleteModal = $('#remove-student-modal');
    });
}());