(function () {

    var passwordStatus;
    var changePassword;
    var newPass;
    var blacklist = [];
    var meter;
    var suggestionElement;

    function showError(message) {
        passwordStatus.css({'background-color': '#f44336', 'color': 'white'});
        passwordStatus.html(message);
        changePassword.attr('disabled', true);
    }

    function showSuccess(message) {
        passwordStatus.css({'background-color': '#4CAF50', 'color': 'white'});
        passwordStatus.html(message);
        changePassword.attr('disabled', false);
    }

    function renderSuggestions(suggestions, warning) {
        if (!warning && !(suggestions.length > 0)) {
            suggestionElement.hide(500);
            return;
        }

        suggestionElement.show(500);

        if (!this.warningWrapper)
            this.warningWrapper = suggestionElement.find('#warning-wrapper');
        
        if (!this.suggestionWrapper)
            this.suggestionWrapper = suggestionElement.find('#suggestion-wrapper');

        if (warning) {
            this.warningWrapper.show();
            suggestionElement.find('#warning').html(warning);
        } else {
            this.warningWrapper.hide();
        }
        
        if (suggestions.length > 0) {
            this.suggestionWrapper.show();
            suggestionElement.find('#suggestions').html(
                $.map(suggestions, function (suggestion) {
                return '<li>'+suggestion+'</li>';
            }).join(''));
        } else {
            this.suggestionWrapper.hide();
        }
    }

    function onChangePasswords(passwordObj) {
        var suggestions = [];
        var warning;

        if (window.zxcvbn && passwordObj.new && passwordObj.new !== newPass) {
            var analysis = zxcvbn(passwordObj.new, blacklist);
            meter.value = analysis.score;
            suggestions = analysis.feedback.suggestions;
            warning = analysis.feedback.warning;
        }

        newPass = passwordObj.new;
        renderSuggestions(suggestions, warning);

        if (passwordObj.isEmpty())
             return;

        if (!passwordObj.hasEnoughLength()) {
            showError('Passwords should be at least 8 characters');
            return;
        }

        if (!passwordObj.passwordsMatch()) {
            showError('Passwords don\'t match');
            return;
        }


        if (window.zxcvbn && meter.value < 2) {
            showError('Password is very weak');
            return;
        }

        showSuccess('All Set!');
    }

    function extractBlacklisted() {
        var blacklisted = $('#blacklist').html();
        if (!blacklisted || blacklisted.length < 1)
            return [];

        var blacklist = [];
        var intermediate = blacklisted.substring(1, blacklisted.length - 1).split(',');
        for (var i = 0; i < intermediate.length; i++) {
            var item = intermediate[i].trim();
            if (item !== '' && item !== 'null')
                blacklist.push(item);
        }

        return blacklist;
    }

    function loadScript(url) {
        var async_load = function() {
            var first, s;
            s = document.createElement('script');
            s.src = url;
            s.type = 'text/javascript';
            s.async = true;
            first = document.getElementsByTagName('script')[0];
            return first.parentNode.insertBefore(s, first);
        };

        if (window.attachEvent) {
            window.attachEvent('onload', async_load);
        } else {
            window.addEventListener('load', async_load, false);
        }
    }

    loadScript('https://cdnjs.cloudflare.com/ajax/libs/zxcvbn/4.4.2/zxcvbn.js');

    /* main */ (function () {
        var oldPass = $('#old-passord');
        var newPass = $('#password');
        var confirmPass = $('#confirm-password');

        // Enable skipping old password field if it doesn't exist
        var skip = !oldPass.length;

        var password = {
            old: null,
            new: null,
            confirm: null,

            isEmpty: function () {
                return !((skip || this.old) && this.new && this.confirm);
            },

            hasEnoughLength: function () {
                return !this.isEmpty() &&
                    (skip || this.old.length >= 8) &&
                    this.new.length >= 8 &&
                    this.confirm.length >= 8
            },

            passwordsMatch: function () {
                return this.new === this.confirm;
            }
        };

        function passwordChangeFactory(type) {
            return function (event) {
                password[type] = event.target.value;
                onChangePasswords(password);
            }
        }

        oldPass.on('keyup', $.debounce(250, passwordChangeFactory('old')));
        newPass.on('keyup', $.debounce(250, passwordChangeFactory('new')));
        confirmPass.on('keyup', $.debounce(250, passwordChangeFactory('confirm')));

        passwordStatus = $('#password-status');
        changePassword = $('#submit');

        blacklist = extractBlacklisted();

        meter = document.getElementById('password-strength-meter');
        suggestionElement = $('.password-suggestions');
    })();
}());