package amu.zhcet.common.utils;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationUtilsTest {

    private Page page;
    private Model model;

    @Before
    public void setUp() {
        page = mock(Page.class);
        model = new ExtendedModelMap();
    }

    @Test
    public void testNormalizePageNull() {
        assertThat(NotificationUtils.normalizePage(null), equalTo(1));
    }

    @Test
    public void testNormalizePageLowerBound() {
        assertThat(NotificationUtils.normalizePage(0), equalTo(1));
        assertThat(NotificationUtils.normalizePage(-1), equalTo(1));
        assertThat(NotificationUtils.normalizePage(-20), equalTo(1));
    }

    @Test
    public void testNormalizePageUpperBound() {
        assertThat(NotificationUtils.normalizePage(1), equalTo(1));
        assertThat(NotificationUtils.normalizePage(24), equalTo(24));
    }

    @Test
    public void testGetSafePage() {
        assertThat(NotificationUtils.getSafePage(-1, 20), equalTo(1));
        assertThat(NotificationUtils.getSafePage(0, 2), equalTo(1));
        assertThat(NotificationUtils.getSafePage(1, 0), equalTo(1));
        assertThat(NotificationUtils.getSafePage(-19, 3), equalTo(1));
        assertThat(NotificationUtils.getSafePage(12, 20), equalTo(12));
        assertThat(NotificationUtils.getSafePage(15, 10), equalTo(10));
    }

    @Test
    public void testSetupModelPageLower() {
        when(page.getTotalPages()).thenReturn(20);

        NotificationUtils.prepareNotifications(model, page, 2);
        Map<String, Object> map = model.asMap();
        assertThat(map.get("minPage"), equalTo(1));
        assertThat(map.get("maxPage"), equalTo(7));
        assertThat(map.get("currentPage"), equalTo(2));
    }

    @Test
    public void testSetupModelPageUpper() {
        when(page.getTotalPages()).thenReturn(20);

        NotificationUtils.prepareNotifications(model, page, 17);
        Map<String, Object> map = model.asMap();
        assertThat(map.get("minPage"), equalTo(12));
        assertThat(map.get("maxPage"), equalTo(20));
        assertThat(map.get("currentPage"), equalTo(17));
    }

    @Test
    public void testSetupModelPageInner() {
        when(page.getTotalPages()).thenReturn(20);

        NotificationUtils.prepareNotifications(model, page, 9);
        Map<String, Object> map = model.asMap();
        assertThat(map.get("minPage"), equalTo(4));
        assertThat(map.get("maxPage"), equalTo(14));
        assertThat(map.get("currentPage"), equalTo(9));
    }

    @Test
    public void testSetupModelPageOuter() {
        when(page.getTotalPages()).thenReturn(5);

        NotificationUtils.prepareNotifications(model, page, 2);
        Map<String, Object> map = model.asMap();
        assertThat(map.get("minPage"), equalTo(1));
        assertThat(map.get("maxPage"), equalTo(5));
        assertThat(map.get("currentPage"), equalTo(2));
    }

    @Test
    public void testSetupModelPageOutBoundUpper() {
        when(page.getTotalPages()).thenReturn(5);

        NotificationUtils.prepareNotifications(model, page, 6);
        Map<String, Object> map = model.asMap();
        assertThat(map.get("minPage"), equalTo(1));
        assertThat(map.get("maxPage"), equalTo(5));
        assertThat(map.get("currentPage"), equalTo(5));
    }

    @Test
    public void testSetupModelPageOutBoundLower() {
        when(page.getTotalPages()).thenReturn(5);

        NotificationUtils.prepareNotifications(model, page, 0);
        Map<String, Object> map = model.asMap();
        assertThat(map.get("minPage"), equalTo(1));
        assertThat(map.get("maxPage"), equalTo(5));
        assertThat(map.get("currentPage"), equalTo(1));
    }

}
