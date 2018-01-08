package amu.zhcet.storage.csv;

import com.j256.simplecsv.processor.CsvProcessor;
import com.j256.simplecsv.processor.ParseError;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class CsvParser<T> {

    private final Class<T> type;
    private final MultipartFile file;

    @Data
    static class Result<T> {
        private final List<String> columns = new ArrayList<>();
        private final List<T> items = new ArrayList<>();
        private final List<ParseError> parseErrors = new ArrayList<>();
        private boolean parsedSuccessfully;
    }

    static <T> CsvParser<T> of(Class<T> type, MultipartFile file) {
        return new CsvParser<>(type, file);
    }

    Result<T> parse() throws IOException {
        Result<T> result = new Result<>();
        CsvProcessor<T> csvProcessor = new CsvProcessor<>(type)
                .withAlwaysTrimInput(true)
                .withIgnoreUnknownColumns(true)
                .withFlexibleOrder(true);

        try {
            result.getColumns().addAll(Arrays.asList(csvProcessor.readHeader(
                    new BufferedReader(new InputStreamReader(file.getInputStream())), null)));
            log.info("Parsing CSV {} with columns: {}", file.getOriginalFilename(), result.getColumns());

            List<T> items = csvProcessor.readAll(new InputStreamReader(file.getInputStream()), result.getParseErrors());

            if (items != null)
                result.getItems().addAll(items);

        } catch (ParseException e) {
            log.warn(String.format("Error Parsing file %s", file.getOriginalFilename()), e);
            ParseError parseError = new ParseError();
            parseError.setErrorType(ParseError.ErrorType.INVALID_HEADER);
            parseError.setMessage(e.getMessage());
            result.getParseErrors().add(parseError);
        }

        if (result.getParseErrors().isEmpty())
            result.setParsedSuccessfully(true);

        return result;
    }

}
