var DataUtils = (function () {
    return {
        searchDelay: function (table) {
            var $searchInput = $('div.dataTables_filter input');

            $searchInput.unbind();

            $searchInput.bind('keyup', $.debounce(1000, function(e) {
                table.search(this.value).draw();
            }));
        },

        fixDate: function(date) {
            return date.split('[')[0];
        },

        setSelectInput: function(id, context, index, selected) {
            $(id).on('change', function () {
                var val = $(this).val();

                var column = context.columns(index);

                if (val !== column.search()) {
                    column.search(val).draw();
                }
            });

            if (selected === '')
                return;

            $(id).find('option[value=' + selected + ']').prop('selected', true);
        },

        getFromLocalStorage: function(defaultVal, index) {
            var key = 'DataTables_studentTable_/dean/students';
            var selected = defaultVal;
            if (key in localStorage) {
                var data = JSON.parse(localStorage.getItem(key));
                selected = data['columns'][index]['search'].search;
            }
            return selected;
        },

        restoreState: function(table, options) {
            for (var i = 0; i < options.length; i++) {
                var option = options[i];
                var statusIndex = table.column(option['columnName'] + ':name').index();
                var selectedStatus = DataUtils.getFromLocalStorage(option['defaultVal'], statusIndex);
                DataUtils.setSelectInput(option['id'], table, statusIndex, selectedStatus);
            }
        }
    }
}());