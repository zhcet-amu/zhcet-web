package amu.zhcet.common.markdown;

import com.vladsch.flexmark.ext.emoji.internal.EmojiReference;
import com.vladsch.flexmark.ext.emoji.internal.EmojiShortcuts;
import org.springframework.stereotype.Service;

@Service
public class EmojiService {

    public String getEmojiUrl(String emojiShortcut) {
        EmojiReference.Emoji emoji = EmojiShortcuts.getEmojiFromShortcut(emojiShortcut);
        return EmojiReference.githubUrl + emoji.githubFile;
    }

}
