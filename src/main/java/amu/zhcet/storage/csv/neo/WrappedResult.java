package amu.zhcet.storage.csv.neo;

import amu.zhcet.storage.csv.CsvParser;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class WrappedResult<T, U> extends Result<T> {
    private final List<Wrapper<U>> items;
    private State state;

    public WrappedResult(Result<T> result, List<Wrapper<U>> items, State state) {
        this.items = items;
        this.state = state;
        setParsed(result.isParsed());
        setMessages(result.getMessages());
        if (result.getCsv().isSuccessful()) {
            CsvParser.Result<T> csv = new CsvParser.Result<>();
            csv.setSuccessful(result.getCsv().isSuccessful());
            // A wrapped result does not have parsed items and errors
            // if parsing was successful
            setCsv(csv);
        } else {
            CsvParser.Result<T> csv = new CsvParser.Result<>(
                    result.getCsv().getItems(),
                    result.getCsv().getParseErrors(),
                    result.getCsv().isSuccessful());
            setCsv(csv);
        }
    }

    public WrappedResult(Result<T> result, List<Wrapper<U>> items) {
        this(result, items, null);
    }

}
