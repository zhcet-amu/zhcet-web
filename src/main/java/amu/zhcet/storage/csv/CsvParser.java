package amu.zhcet.storage.csv;

import com.j256.simplecsv.processor.CsvProcessor;
import com.j256.simplecsv.processor.ParseError;
import lombok.*;
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
public class CsvParser<T> {

    private final Class<T> type;

    @Data
    public static class Result<T> {
        private final List<T> items;
        private final List<ParseError> parseErrors;
        private boolean successful;

        public Result() {
            this.items = new ArrayList<>();
            this.parseErrors = new ArrayList<>();
        }

        public Result(List<T> items, List<ParseError> parseErrors, boolean successful) {
            this.items = items;
            this.parseErrors = parseErrors;
            this.successful = successful;
        }

    }

    public static <T> CsvParser<T> of(Class<T> type) {
        return new CsvParser<>(type);
    }

    public Result<T> parse(MultipartFile file) throws IOException {
        return parse(file.getInputStream());
    }

    public Result<T> parse(InputStream inputStream) throws IOException {
        return parse(new InputStreamReader(inputStream));
    }

    public Result<T> parse(Reader reader) throws IOException {
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
            result.setSuccessful(true);

        return result;
    }

}
