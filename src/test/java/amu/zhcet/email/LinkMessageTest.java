package amu.zhcet.email;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LinkMessageTest {

    @Test
    public void testSingleBcc() {
        LinkMessage linkMessage = new LinkMessage.LinkMessageBuilder()
                .bcc("jamla")
                .build();

        assertEquals("jamla", linkMessage.getRecipientEmail());
        assertNull(linkMessage.getBcc());
    }

    @Test
    public void testBccWithRecipient() {
        LinkMessage linkMessage = new LinkMessage.LinkMessageBuilder()
                .recipientEmail("cari")
                .bcc("jamla", "papu")
                .build();

        String[] emails = {"jamla", "papu"};
        assertEquals("cari", linkMessage.getRecipientEmail());
        assertArrayEquals(emails, linkMessage.getBcc());
    }

    @Test
    public void testRotatingBcc() {
        LinkMessage linkMessage = new LinkMessage.LinkMessageBuilder()
                .bcc("cari", "jamla", "papu")
                .build();

        String[] emails = {"jamla", "papu"};
        assertEquals("cari", linkMessage.getRecipientEmail());
        assertArrayEquals(emails, linkMessage.getBcc());
    }

    @Test
    public void testNoBcc() {
        LinkMessage linkMessage = new LinkMessage.LinkMessageBuilder()
                .recipientEmail("jamla")
                .build();

        assertEquals("jamla", linkMessage.getRecipientEmail());
        assertNull(linkMessage.getBcc());
    }

    @Test
    public void testListBcc() {
        LinkMessage linkMessage = new LinkMessage.LinkMessageBuilder()
                .bcc(Arrays.asList("cari", "jamla", "papu"))
                .build();

        String[] emails = {"jamla", "papu"};
        assertEquals("cari", linkMessage.getRecipientEmail());
        assertArrayEquals(emails, linkMessage.getBcc());
    }
}