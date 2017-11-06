package in.ac.amu.zhcet.service.markdown;

import com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension;
import com.vladsch.flexmark.ext.autolink.AutolinkExtension;
import com.vladsch.flexmark.ext.emoji.EmojiExtension;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.superscript.SuperscriptExtension;
import com.vladsch.flexmark.util.KeepType;
import com.vladsch.flexmark.util.options.MutableDataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.vladsch.flexmark.ext.anchorlink.AnchorLinkExtension.ANCHORLINKS_ANCHOR_CLASS;

@Component
public class MarkDownOptions {

    @Bean
    public MutableDataHolder options() {
        return new MutableDataSet()
                .set(Parser.REFERENCES_KEEP, KeepType.LAST)
                .set(HtmlRenderer.INDENT_SIZE, 2)
                .set(HtmlRenderer.PERCENT_ENCODE_URLS, true)
                .set(HtmlRenderer.GENERATE_HEADER_ID, true)
                .set(HtmlRenderer.ESCAPE_HTML, true)

                // for full GFM table compatibility add the following table extension options:
                .set(TablesExtension.COLUMN_SPANS, false)
                .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
                .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
                .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true)
                // anchor links
                .set(ANCHORLINKS_ANCHOR_CLASS, "md-header-link")
                // emoji
                .set(EmojiExtension.USE_IMAGE_URLS, true)
                // extensions
                .set(Parser.EXTENSIONS, Arrays.asList(
                        TablesExtension.create(),
                        AutolinkExtension.create(),
                        AnchorLinkExtension.create(),
                        EmojiExtension.create(),
                        StrikethroughExtension.create(),
                        TaskListExtension.create(),
                        SuperscriptExtension.create()
                ));
    }

    @Bean
    public Parser parser(MutableDataHolder options) {
        return Parser.builder(options).build();
    }

    @Bean
    public HtmlRenderer renderer(MutableDataHolder options) {
        return HtmlRenderer.builder(options).build();
    }

}
