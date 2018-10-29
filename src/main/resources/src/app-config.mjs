function disableInput(disable) {
    $('#session-term').attr('disabled', disable);
    $('#session-year').attr('disabled', disable);
}

function hideElement(hide) {
    $('#default-session').attr('hidden', hide);
}

const auto = $('#auto');
disableInput(auto.is(':checked'));

auto.change(function () {
    disableInput(this.checked);
    hideElement(!this.checked);
});

// Thymeleaf does not let you submit disabled values, so remove disabled tag before submit
$('form').submit(function(e) {
    $(':disabled').each(function(e) {
        $(this).removeAttr('disabled');
    })
});