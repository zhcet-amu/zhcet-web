package amu.zhcet.core.dean.datatables;

import com.google.common.base.Strings;
import org.springframework.data.jpa.datatables.mapping.Column;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;

import java.util.Optional;

class DataTableUtils {

    public static void convertInput(DataTablesInput input) {
        input.getColumns().replaceAll(column -> {
            column.setData(column.getData().replace('_', '.'));
            return column;
        });
    }

    /**
     * Workaround for boolean column filtering as default library version does not work correctly
     * @param input DataTablesInput : Input to be sanitized and source of the boolean to be returned
     * @param columnName String : Boolean column name to be cleared
     * @return Boolean : Set value of columnName before sanitizing
     */
    public static Boolean sanitizeBoolean(DataTablesInput input, String columnName) {
        Optional<Column> columnOptional = input.getColumns()
                .stream()
                .filter(column -> column.getName().equals(columnName))
                .findFirst();

        if (!columnOptional.isPresent())
            return null;

        Column column = columnOptional.get();
        String value = column.getSearch().getValue();
        Boolean stored = Strings.isNullOrEmpty(value) ? null : Boolean.parseBoolean(value);
        column.getSearch().setValue(null);

        return stored;
    }


}
