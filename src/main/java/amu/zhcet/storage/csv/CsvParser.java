package amu.zhcet.storage.csv;

import com.j256.simplecsv.processor.CsvProcessor;
import com.j256.simplecsv.processor.ParseError;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class CsvParser<T> {

    private final Class<T> type;

    @Data
    static class Result<T> {
        private final List<T> items = new ArrayList<>();
        private final List<ParseError> parseErrors = new ArrayList<>();
        private boolean parsedSuccessfully;
    }

    static <T> CsvParser<T> of(Class<T> type) {
        return new CsvParser<>(type);
    }

    Result<T> parse(MultipartFile file) throws IOException {
        return parse(file.getInputStream());
    }

    Result<T> parse(InputStream inputStream) throws IOException {
        return parse(new InputStreamReader(inputStream));
    }

    Result<T> parse(Reader reader) throws IOException {
        Result<T> result = new Result<>();
        CsvProcessor<T> csvProcessor = new CsvProcessor<>(type)
                .withAlwaysTrimInput(true)
                .withIgnoreUnknownColumns(true)
                .withFlexibleOrder(true);


        List<T> items = null;
        try {
            items = csvProcessor.readAll(reader, result.getParseErrors());
        } catch (ParseException e) {
            ParseError parseError = new ParseError();
            parseError.setErrorType(ParseError.ErrorType.INTERNAL_ERROR);
            parseError.setMessage(e.getMessage());
            result.getParseErrors().add(parseError);
        }

        if (items != null)
            result.getItems().addAll(items);

        if (result.getParseErrors().isEmpty())
            result.setParsedSuccessfully(true);

        return result;
    }

}
