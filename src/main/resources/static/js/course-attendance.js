(function () {
    $(document).ready(function () {
        $.fn.dataTable.ext.search.push(
            function( settings, data, dataIndex ) {
                var min = parseInt( $('#min').val(), 10 );
                var max = parseInt( $('#max').val(), 10 );
                var perc = parseFloat( data[5] ) || 0; // use data for the age column

                return ( isNaN(min) && isNaN(max) ) ||
                    ( isNaN(min) && perc <= max ) ||
                    ( min <= perc && isNaN(max) ) ||
                    ( min <= perc && perc <= max );

            }
        );

        var table = $('#attendance_table').DataTable({
            order: [],
            dom: 'lBfrtip',
            buttons: [
                'copy', 'csv', 'excel', 'pdf', 'print'
            ]
        });

        $('#min, #max').keyup( function() {
            table.draw();
        } );

        $("#upload_modal").modal('show');
    });
}());