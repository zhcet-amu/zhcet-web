package amu.zhcet.common.markdown;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EmojiServiceTest {

    private EmojiService emojiService;

    @Before
    public void setUp() {
        emojiService = new EmojiService();
    }

    @Test
    public void testGetEmoji() {
        String emoji = emojiService.getEmojiUrl("+1");
        assertEquals("https://assets-cdn.github.com/images/icons/emoji/unicode/1f44d.png?v7", emoji);
    }

}