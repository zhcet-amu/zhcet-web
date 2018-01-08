package amu.zhcet.common.markdown;

import com.vladsch.flexmark.ext.emoji.internal.EmojiCheatSheet;
import org.springframework.stereotype.Service;

@Service
public class EmojiService {

    public String getEmojiUrl(String emoji) {
        EmojiCheatSheet.EmojiShortcut shortcut = EmojiCheatSheet.shortCutMap.get(emoji);

        if (shortcut != null)
            return shortcut.url;
        return null;
    }

}
