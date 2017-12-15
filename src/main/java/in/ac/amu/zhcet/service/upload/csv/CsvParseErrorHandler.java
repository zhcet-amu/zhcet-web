package in.ac.amu.zhcet.service.upload.csv;

import com.google.common.base.Strings;
import com.j256.simplecsv.processor.ParseError;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static in.ac.amu.zhcet.service.upload.csv.AbstractUploadService.ParseErrorWrapper;

public class CsvParseErrorHandler {

    public static Optional<String> getErrorColumn(ParseError parseError, List<String> columns) {
        String line = parseError.getLine();
        if (Strings.isNullOrEmpty(line) || columns == null || columns.isEmpty())
            return Optional.empty();

        List<String> items = Arrays.asList(line.trim().split(","));
        int index = items.indexOf(parseError.getMessage());
        if (index == -1 || index > columns.size() - 1)
            return Optional.empty();

        return Optional.of(columns.get(index));
    }

    public static Optional<String> getInvalidFormatColumn(ParseErrorWrapper parseErrorWrapper) {
        if (parseErrorWrapper.getParseError().getErrorType().equals(ParseError.ErrorType.INVALID_FORMAT))
            return getErrorColumn(parseErrorWrapper.getParseError(), parseErrorWrapper.getColumns());
        else
            return Optional.empty();
    }

    public static String generateEnumError(ParseError parseError, String type, Enum[] values) {
        return type + " should be among these : " +
                Arrays.asList(values).toString() +
                "<br> Found: " +
                getErrorMessage(parseError);
    }

    public static String getErrorMessage(ParseError parseError) {
        String message = parseError.getMessage()
                .replace("suppled", "supplied") +
                "<br>Line Number: " + parseError.getLineNumber() +
                " Position: " + parseError.getLinePos();

        if (parseError.getLine() != null)
            message += "<br>Line: " + parseError.getLine();

        return message;
    }

    public static String handleError(ParseErrorWrapper parseErrorWrapper) {
        if (parseErrorWrapper == null || parseErrorWrapper.getParseError() == null)
            return null;
        ParseError parseError = parseErrorWrapper.getParseError();
        getInvalidFormatColumn(parseErrorWrapper)
                .ifPresent(column ->
                        parseError.setMessage(String.format("%s is not of correct type : %s",
                                parseError.getMessage(), column)));

        return getErrorMessage(parseError);
    }

}
