(function ($) {
    var interval;

    function callIfFunction(func, arg) {
        if ($.isFunction(func))
            func(arg);
    }

    function handleData(settings, result, close) {
        callIfFunction(settings.each, result);

        if (result.started && !settings.hid) {
            settings.hid = true;
            settings.waitingComponent.hide();
        }
        var completed = result.completed;
        var total = result.total;
        var percentage = (completed/total*100).toFixed(2) + '%';
        settings.completedText.text(completed);
        settings.totalText.text(total);
        settings.percentageText.text(percentage);
        settings.progressBar.css('width', percentage);

        if (result.invalid || result.finished || result.failed) {
            callIfFunction(close);
        }

        if (result.finished) {
            callIfFunction(settings.finished, result);
        } else if(result.failed || result.invalid) {
            callIfFunction(settings.failed, result);
        }
    }

    function poll(settings) {
        var ajaxCall = $.ajax({
            url: settings.baseUrl + settings.taskId,
            dataType: 'json',
            type: 'get'
        });

        Promise.resolve(ajaxCall).then(function(result) {
            handleData(settings, result, function () {
                clearInterval(interval);
            });
        }).catch(function(error) {
            callIfFunction(settings.error, error);
            clearInterval(interval);
        });
    }

    function startPolling(settings) {
        interval = setInterval(function () {
            poll(settings);
        }, settings.delay);
        poll(settings);
    }

    function startListening(settings) {
        var eventSource = new EventSource('/management/task/sse/' + settings.taskId);

        eventSource.onmessage = function(result) {
            handleData(settings, JSON.parse(result.data), function () {
                eventSource.close();
            });
        };

        eventSource.onerror = function (error) {
            console.log(error);
            callIfFunction(settings.failed, error);
            eventSource.close();
        }
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

            settings.waitingComponent = container.find('.waiting');
            settings.completedText = container.find('.completed');
            settings.totalText = container.find('.total');
            settings.percentageText = container.find('.percentage');
            settings.progressBar = container.find('.determinate');

            if (settings.taskId && settings.taskId !== '') {
                container.show();

                if (!!window.EventSource) {
                    startListening(settings);
                } else {
                    callIfFunction(settings.error,
                        'Please update your browser, it is very old. ' +
                        'We recommend new versions of Chrome or Firefox');
                    startPolling(settings);
                }
            }
        });
    }

}(jQuery));