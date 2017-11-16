var Login = (function ($) {
    // Default Slide Images
    var slides = [];

    function updateSlides(slides) {
        $('.pattern-bg').hide();
        $.backstretch(slides, { duration: 2000, fade: 750 });
    }

    function setupSlideShow() {
        var database = firebase.database();
        database.ref('login/slides').on('value', function (snapshot) {
            updateSlides(snapshot.val())
        });
    }

    function setupLoginFlow() {
        Authentication.auth().signOut();
        var checking = $('#checking');
        var loginPanel = $('#login-panel');
        var loaderMessage = $('#loader-message');

        loginPanel.find('#google-login').click(function () {
            checking.show();
            loginPanel.hide();
            loaderMessage.html('Redirecting to Google...');
            Authentication.google().login(function (success, message) {
                if (success) {
                    loaderMessage.html('Trying to Login. Please wait...');
                } else {
                    loginPanel.show();
                    if (message)
                        loaderMessage.html(message);
                    else
                        checking.hide();
                }
            });
        });
    }

    $(document).ready(function() {
        setupSlideShow();
    });

    return {
        startLoginFlow: setupLoginFlow
    }
}(jQuery));