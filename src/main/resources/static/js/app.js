$(document).ready(function() {

    $('input').blur(function() {
        if ($(this).val())
            $(this).addClass('used');
        else
            $(this).removeClass('used');
    });

    $(this).find('input.mtrl').after('<span class="highlight"></span><span class="bar"></span>');
});