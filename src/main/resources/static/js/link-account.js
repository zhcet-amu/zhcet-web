(function ($, Authentication) {
    var btn = $('#link-google');
    var loader = $('#loader');
    var listener;

    var googleLink = Authentication.google.link();

    function setListener(func) {
        if (listener)
            btn.off('click', listener);
        listener = func;
        btn.on('click', listener);
    }

    function showLoader(show) {
        loader.css('visibility', show ? 'visible' : 'hidden');
    }

    function setState(linked) {
        if (linked) {
            btn.html('Unlink Google Account');
            setListener(function () {
                showLoader(true);
                googleLink.unlinkGoogle(function (success, error) {
                    showLoader(false);
                    if (success) {
                        setState(false);
                        toastr.success('Unlinked Successfully');
                    } else if (error) {
                        toastr.error(error.message);
                    }
                })
            })
        } else {
            btn.html('Link Google Account');
            setListener(function () {
                showLoader(true);
                googleLink.linkGoogle(function (success, error) {
                    showLoader(false);
                    if (success) {
                        setState(true);
                        toastr.success('Linked Successfully');
                    } else if (error) {
                        toastr.error(error.message);
                    }
                })
            })
        }
    }

    googleLink.isGoogleLinked(function (linked) {
        if (linked !== undefined) {
            btn.show();
            setState(linked);
        } else {
            btn.hide();
        }
    });
}(jQuery, Authentication));