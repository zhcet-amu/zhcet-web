(function ($) {
    const updateFunc = function () {
        update(this.parentElement.parentElement);
    };

    $('input.attendance')
        .on('keyup', updateFunc)
        .on('click', updateFunc);

    const confirmButton = $('#attendance-confirm');

    function update(node) {
        var cell = $(node);

        var attended = cell.find('.attended').val();
        var delivered = cell.find('.delivered').val();
        var percent = attended/delivered*100;

        if (percent < threshold)
            cell.addClass('bg-danger');
        else
            cell.removeClass('bg-danger');

        cell.find('.percent').text(truncateDecimals(percent, 2) + '%');

        const errorInfo = cell.find('.error-info');
        if (percent > 100) {
            cell.addClass('bg-warning');
            errorInfo.show();
            confirmButton.attr('disabled', true);
        } else {
            cell.removeClass('bg-warning');
            errorInfo.hide();
            confirmButton.attr('disabled', false);
        }
    }

    $('.eq').on('click', function () {
       const delivered = $(this.parentElement).find('.delivered').val();
       $('.delivered').val(delivered);
    });

    var truncateDecimals = function (number, digits) {
        var multiplier = Math.pow(10, digits),
            adjustedNum = number * multiplier,
            truncatedNum = Math[adjustedNum < 0 ? 'ceil' : 'floor'](adjustedNum);

        return truncatedNum / multiplier;
    };

    $.fn.dataTable.ext.search.push(
        function(settings, data) {
            var percentIndex = 6; // Index of percentage column in table

            var min = parseInt( $('#min').val(), 10 );
            var max = parseInt( $('#max').val(), 10 );
            var perc = parseFloat( data[percentIndex] ) || 0; // use data for the % column

            return ( isNaN(min) && isNaN(max) ) ||
                ( isNaN(min) && perc <= max ) ||
                ( min <= perc && isNaN(max) ) ||
                ( min <= perc && perc <= max );

        }
    );

    $('#error-table').DataTable();
    var table = $('#attendance_table').DataTable({
        order: [],
        dom: 'lBfrtip',
        buttons: [
            'copy', 'csv', 'excel', 'pdf', 'print'
        ]
    });

    $('#min, #max').keyup( function() {
        table.draw();
    });

    $("#upload_modal").modal('show');
    $("#error-modal").modal('show');
}(jQuery));