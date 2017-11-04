var DataUtils = (function () {

    function setSelectInput(id, context, index, selected) {
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
    }

    function getFromLocalStorage(localStorageKey, defaultVal, index) {
        var selected = defaultVal;
        if (localStorageKey in localStorage) {
            var data = JSON.parse(localStorage.getItem(localStorageKey));
            selected = data['columns'][index]['search'].search;
        }
        return selected;
    }

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

        restoreState: function(table, localStorageKey, options) {
            for (var i = 0; i < options.length; i++) {
                var option = options[i];
                var statusIndex = table.column(option['columnName'] + ':name').index();
                var selectedStatus = getFromLocalStorage(localStorageKey, option['defaultVal'], statusIndex);
                setSelectInput(option['id'], table, statusIndex, selectedStatus);
            }
        }
    }
}());