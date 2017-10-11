function getImageUrl(url) {
    if (url && url !== '')
        return url;
    return 'https://zhcet-web-amu.firebaseapp.com/static/img/account.svg';
}

function itemTemplate(suggestion) {
    return [
        "<div class='suggestion'>",
        '<span><img src="' + getImageUrl(suggestion._item.userDetailsAvatarUrl) + '" class="avatar-img rounded-circle"/></span>',
        "<span class='pad-top name'>", fuzzyhound.highlight(suggestion.userName,"userName") ,"</span> ",
        "<span class='pad-top faculty-id'><i>(", fuzzyhound.highlight(suggestion.facultyId, "facultyId"), ")</i></span>",
        "<span class='pad-top department capsule'>", suggestion._item.userDepartmentName, "</span> ",
        "</div>"
    ].join("");
}

function addItem(template, item) {
    item.userDetailsAvatarUrl = getImageUrl(item.userDetailsAvatarUrl);
    $('.in-charge-container').append(tmpl(template, item));
    attachRemove();
}

function attachRemove() {
    $('.remove').click(function (event) {
        $(event.target).closest('.in-charge').remove();
    });
}

var all = false;
var index = ["facultyId", "userName"];
var baseUrl = "/department/api/faculty";
setsource(baseUrl, index);

$(window).load(function () {
    $('#faculty-modal').modal('show');
    $('#registration-modal').modal('show');
    $('#registrationTable').dataTable({
        scrollY:        true,
        scrollCollapse: true,
        "order": []
    });
    $('#confirmRegistrationTable').dataTable({
        scrollY:        true,
        scrollCollapse: true,
        "order": []
    });

    var inChargeTemplate = $('#in-charge-template').html();

    // Unfloat Code
    var deleteCode = $('#delete-code');
    var deleteButton = $('#delete-button');
    var code = $('#course-code').val();
    deleteCode.on('keyup', function () {
        deleteButton.prop('disabled', deleteCode.val() !== code);
    });

    // Search In-Charge
    var searchBox = $('.incharges');
    searchBox.typeahead({
        highlight: false,
        minLength: 2
    }, {
        name: 'incharges',
        limit: 100,
        source: fuzzyhound,
        display: "item.userName",
        templates: {
            suggestion: itemTemplate
        }
    }).on('typeahead:select', function (ev, suggestion) {
        addItem(inChargeTemplate, suggestion._item);
    });

    var toggle = $('#toggle-more');
    toggle.click(function () {
        all = !all;
        setsource(baseUrl + '?all=' + all, index);
        toggle.text(all ? 'Less' : 'More');
    });

    attachRemove();
});