package amu.zhcet.common.markdown;

import com.google.common.base.Strings;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MarkDownService {

    private final Parser parser;
    private final HtmlRenderer renderer;

    @Autowired
    public MarkDownService(Parser parser, HtmlRenderer renderer) {
        this.parser = parser;
        this.renderer = renderer;
    }

    public String render(String input) {
        input = Strings.nullToEmpty(input);
        return renderer.render(parser.parse(input));
    }

}
