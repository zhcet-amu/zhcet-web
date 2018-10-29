import fuzzyhound from './fuzzyhound';
import { blockUI } from "../app/utils";

function getImageUrl(url) {
    if (url && url !== '')
        return url;
    return '/img/account.svg';
}

function itemTemplate(suggestion) {
    return [
        "<div class='suggestion'>",
        '<span><img src="' + getImageUrl(suggestion._item.userDetailsAvatarUrl) + '" class="avatar-img rounded-circle"/></span>',
        "<span class='pad-top name'>", fuzzyhound.get().highlight(suggestion.userName,"userName") ,"</span> ",
        "<span class='pad-top faculty-id'><i>(", fuzzyhound.get().highlight(suggestion.facultyId, "facultyId"), ")</i></span>",
        "<span class='pad-top department capsule p-small'>", suggestion._item.userDepartmentName, "</span> ",
        "</div>"
    ].join("");
}

function attachRemove() {
    $('.remove').click(function (event) {
        $(event.target).closest('.in-charge').remove();
    });
}

function addItem(template, item) {
    item.userDetailsAvatarUrl = getImageUrl(item.userDetailsAvatarUrl);
    $('.in-charge-container').append(tmpl(template, item));
    attachRemove();
}

let all = false;
const department = PageDetails.department;
const index = ["facultyId", "userName"];
const baseUrl = "/admin/department/" + department + "/api/faculty";
const actionArea = $('#incharge-action');

blockUI(actionArea);

fuzzyhound.setSource(baseUrl, index, function () {
    actionArea.unblock();
});

$('#faculty-modal').modal('show');

const inChargeTemplate = $('#in-charge-template').html();

// Unfloat Code
const deleteCode = $('#delete-code');
const deleteButton = $('#delete-button');
const code = $('#course-code').val();
deleteCode.on('keyup', function () {
    deleteButton.prop('disabled', deleteCode.val() !== code);
});

// Search In-Charge
const searchBox = $('.incharges');
searchBox.typeahead({
    highlight: false,
    minLength: 2
}, {
    name: 'incharges',
    limit: 100,
    source: fuzzyhound.get(),
    display: "item.userName",
    templates: {
        suggestion: itemTemplate
    }
}).on('typeahead:select', function (ev, suggestion) {
    addItem(inChargeTemplate, suggestion._item);
});

const toggle = $('#toggle-more');
toggle.click(function () {
    all = !all;
    App.blockUI(actionArea);
    fuzzyhound.setSource(baseUrl + '?all=' + all, index, function () {
        actionArea.unblock();
    });
    toggle.text(all ? 'Less' : 'More');
});

attachRemove();
