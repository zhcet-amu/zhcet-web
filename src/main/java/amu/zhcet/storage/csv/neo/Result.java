package amu.zhcet.storage.csv.neo;

import amu.zhcet.storage.csv.CsvParser;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Result<T> {
    private CsvParser.Result<T> csv;
    private List<Message> messages = new ArrayList<>();
    private boolean parsed = false;
}
