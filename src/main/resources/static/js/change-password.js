var Checker = (function () {

    var newPass;
    var blacklist = [];
    var weaknessPanel, weaknessStatus, weaknessStatusText, weaknessReport;
    var passwordProgress;

    function getDefaultResponse(password) {
        return {
            value: password,
            valid: true
        };
    }

    function getWeaknessStatus(analysis) {
        switch (analysis.score) {
            case 0:
                return 'Extremely Weak';
            case 1:
                return 'Weak';
            case 2:
                return 'OK';
            case 3:
                return 'Good';
            case 4:
                return 'Very Good';
            default:
                return 'No Status';
        }
    }

    function getProgressColor(analysis) {
        switch (analysis.score) {
            case 1:
                return '#DA4453';
            case 2:
                return '#FB8C00';
            case 3:
                return '#FFD600';
            case 4:
                return '#37BC9B';
            default:
                return 'red';
        }
    }

    function getSuggestions(analysis) {
        var suggestions = analysis.feedback.suggestions || [];
        return $.map(suggestions, function (suggestion) {
            if (suggestion !== '')
                return '<li>'+suggestion+'</li>';
        }).join('');
    }

    function generateReport(analysis) {
        var report = 'Your password can be cracked in <em><strong>'
            + analysis.crack_times_display.offline_slow_hashing_1e4_per_second +
            '</strong></em> on fast computers<br><br>';
        report += 'Guesses : <em>' + analysis.guesses + '</em><br><br>';

        report += 'Cracking Times : <br><strong>Fastest</strong>: <em>' + analysis.crack_times_display.offline_fast_hashing_1e10_per_second +
            ' (' + analysis.crack_times_seconds.offline_fast_hashing_1e10_per_second + ' s)</em><br>' +

            '<strong>Fast</strong>: <em>' + analysis.crack_times_display.offline_slow_hashing_1e4_per_second +
            ' (' + analysis.crack_times_seconds.offline_slow_hashing_1e4_per_second + ' s)</em><br>' +

            '<strong>Slow</strong>: <em>' + analysis.crack_times_display.online_no_throttling_10_per_second +
            ' (' + analysis.crack_times_seconds.online_no_throttling_10_per_second + ' s)</em><br>' +

            '<strong>Slower</strong>: <em>' + analysis.crack_times_display.online_throttling_100_per_hour +
            ' (' + analysis.crack_times_seconds.online_throttling_100_per_hour + ' s)</em><br><br>';

        var suggestions = getSuggestions(analysis);
        if (suggestions !== '') {
            report += '<strong>Suggestions</strong>';
            report += '<ul>' + suggestions + '</ul>'
        }

        return report;
    }

    function renderAnalysis(analysis) {
        weaknessStatus.show();
        weaknessStatusText.text(getWeaknessStatus(analysis));
        passwordProgress.css('background-color', getProgressColor(analysis));
        passwordProgress.css('width', analysis.score/4*100 + '%');

        weaknessReport.attr('data-content', generateReport(analysis));
    }

    function getWarning(analysis) {
        var warning = analysis.feedback.warning;
        return warning !== '' ? warning: "Weak Password";
    }

    function getValidity(analysis) {
        var response = getDefaultResponse(analysis.password);
        response.valid = analysis.score > 1;
        response.message = getWarning(analysis);
        return response;
    }

    function checkPasswordStrength(password, callback) {
        // If zxcvbn is not loaded, return password as valid
        if (!window.zxcvbn || password === newPass) {
            callback(getDefaultResponse(password));
            return;
        }

        newPass = password;
        var analysis = zxcvbn(password, blacklist);

        callback(getValidity(analysis));
        renderAnalysis(analysis);
    }

    /* main */ (function () {
        weaknessPanel = $('.suggestions');
        weaknessStatus = weaknessPanel.find('#weakness-status');
        weaknessStatusText = weaknessStatus.find('#status-text');
        weaknessReport = weaknessStatus.find('#weakness-report');
        passwordProgress = weaknessPanel.find('#password-progress');

        loadScript('https://cdnjs.cloudflare.com/ajax/libs/zxcvbn/4.4.2/zxcvbn.js', function () {
            // Show suggestions only when zxcvbn is loaded
            weaknessPanel.show();
        });

        blacklist = PageDetails.blacklisted;

        $('[data-toggle="popover"]').popover();
        $("input,select,textarea").not("[type=submit]").jqBootstrapValidation();
    }());

    return {
        check: checkPasswordStrength
    }
}());

function zxcvbn_callback($el, value, callback) {
    // Check password and show warnings
    Checker.check(value, callback);
}