import { init } from "./authentication/authentication";
import './authentication/link-account';

$('.file-uploader').initUpload({
    fileTypes: ['image/png', 'image/jpeg', 'image/jpg', 'image/bmp', 'image/gif'],
    error: function (message) {
        toastr.error(message);
    }
});

init();

// Tab Selection

const groupItem = $('.list-group-item');
groupItem.on('click', function () {
    groupItem.removeClass('active');
    $(this).tab('show');
});

function hashChange() {
    var hash = window.location.hash;
    if (!hash) {
        hash = '#profile';
    }

    $(hash + '-tab').tab('show');
}

window.onhashchange = hashChange;
hashChange();

// Validation
$("input,select,textarea").not("[type=submit]").jqBootstrapValidation();
