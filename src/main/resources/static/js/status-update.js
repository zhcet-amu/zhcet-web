(function ($) {
    var interval;

    function callIfFunction(func, arg) {
        if ($.isFunction(func))
            func(arg);
    }

    function poll(settings) {
        var ajaxCall = $.ajax({
            url: settings.baseUrl + settings.taskId,
            dataType: 'json',
            type: 'get'
        });

        Promise.resolve(ajaxCall).then(function(result) {
            callIfFunction(settings.each, result);

            var completed = result.completed;
            var total = result.total;
            var percentage = (completed/total*100).toFixed(2) + '%';
            settings.completedText.text(completed);
            settings.totalText.text(total);
            settings.percentageText.text(percentage);
            settings.progressBar.css('width', percentage);

            if (result.invalid || result.finished || result.failed) {
                clearInterval(interval);
                callIfFunction(settings.error, result);
            }

            if (result.finished)
                callIfFunction(settings.finished, result);
            else if(result.failed || result.invalid)
                callIfFunction(settings.failed, result);
        }).catch(function(error) {
            callIfFunction(settings.error, error);
            clearInterval(interval);
        });
    }

    $.fn.initStatusProgress = function (options) {
        var settings = $.extend({
            taskId: '',
            baseUrl: '/management/task/status/',
            delay: 2000,
            each: null,
            error: null,
            success: null,
            failed: null,
            finished: null
        }, options);

        return this.each(function () {
            var container = $(this);

            settings.completedText = container.find('.completed');
            settings.totalText = container.find('.total');
            settings.percentageText = container.find('.percentage');
            settings.progressBar = container.find('.determinate');

            if (settings.taskId && settings.taskId !== '') {
                container.show();
                interval = setInterval(function () {
                    poll(settings);
                }, settings.delay);
                poll(settings);
            }
        });
    }

}(jQuery));