(function () {
    $(document).ready(function () {
        var btn = $('#link-google');
        var loader = $('#loader');
        var listener;

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
                    Authentication.unlinkGoogle(function (success, error) {
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
                    Authentication.linkGoogle(function (success, error) {
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

        Authentication.isGoogleLinked(function (linked) {
            if (linked !== undefined) {
                btn.show();
                setState(linked);
            } else {
                btn.hide();
            }
        });
    });
}());