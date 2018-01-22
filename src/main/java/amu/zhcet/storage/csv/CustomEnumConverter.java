package amu.zhcet.storage.csv;

import com.j256.simplecsv.converter.EnumConverter;
import com.j256.simplecsv.processor.ColumnInfo;
import com.j256.simplecsv.processor.ParseError;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CustomEnumConverter extends EnumConverter {

    @Override
    public Enum<?> stringToJava(String line, int lineNumber, int linePos, ColumnInfo<Enum<?>> columnInfo, String value, ParseError parseError) {
        Enum<?> enumType = super.stringToJava(line, lineNumber, linePos, columnInfo, value, parseError);

        if (parseError.isError()) {
            String possibleValues = Arrays.stream(columnInfo.getType().getEnumConstants())
                    .map(Enum::toString)
                    .collect(Collectors.joining(", "));
            parseError.setMessage(value + " can't be converted to column " + columnInfo.getColumnName() +
                    "(" + columnInfo.getType().getSimpleName() + "). Possible Values : " + possibleValues);
        }

        return enumType;
    }
}
