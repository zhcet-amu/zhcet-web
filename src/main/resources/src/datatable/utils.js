function setSelectInput(id, table, index, selected) {
    $(id).on('change', function () {
        const val = $(this).val();
        const column = table.columns(index);

        if (val !== column.search()) {
            column.search(val).draw();
        }
    });

    if (selected === '')
        return;

    $(id).find('option[value=' + selected + ']').prop('selected', true);
}

function getFromLocalStorage(localStorageKey, defaultVal, index) {
    if (localStorageKey in localStorage) {
        const data = JSON.parse(localStorage.getItem(localStorageKey));
        return data.columns[index].search.search;
    }
    return defaultVal;
}

export function searchDelay(table) {
    const $searchInput = $('div.dataTables_filter input');

    $searchInput.unbind();

    $searchInput.bind('keyup change', $.debounce(500, function() {
        table.search(this.value).draw();
    }));
}

export function attachSelectors(table, localStorageKey, options) {
    for (let i = 0; i < options.length; i++) {
        const option = options[i];
        const statusIndex = table.column(option.name + ':name').index();
        const selectedStatus = getFromLocalStorage(localStorageKey, option.defaultVal, statusIndex);
        setSelectInput(option.id, table, statusIndex, selectedStatus);
    }
}

export function getSearchConfig(columns, options) {
    return columns.map(column => {
        const option = options.filter(option => column.name === option.name)[0];
        if (option && option.defaultVal) {
            return {
                sSearch: option.defaultVal || null
            }
        }
        return null
    });
}

export function addSearchColumns(table) {
    const settings = table.settings()[0];
    const stateKey = `DataTables_${ settings.sInstance }_${ window.location.pathname }`;

    table.columns().every(function () {
        const setting = settings.aoColumns[this.index()];

        if (setting.searchable === false)
            return;

        const defaultValue = settings.aoPreSearchCols[this.index()].sSearch;
        const value = getFromLocalStorage(stateKey, defaultValue, this.index());

        const footer = this.footer();
        const title = footer.innerText;
        footer.innerHTML = `<input type="text" placeholder="${ title }" value="${ value }" />`;

        const that = this;

        $('input', this.footer()).on('keyup change', $.debounce(500, function () {
            if (that.search() !== this.value) {
                that.search(this.value).draw();
            }
        }));
    });
}
