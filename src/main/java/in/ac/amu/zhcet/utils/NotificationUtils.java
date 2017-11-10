package in.ac.amu.zhcet.utils;

import org.springframework.data.domain.Page;
import org.springframework.ui.Model;

public class NotificationUtils {

    // Prevent instantiation of Util class
    private NotificationUtils() { }

    public static int normalizePage(Integer page) {
        if (page == null || page < 1)
            return 1;
        return page;
    }

    public static void prepareNotifications(Model model, Page<?> page, int currentPage) {
        int minPage = Math.max(1, currentPage - 5);
        int maxPage = Math.max(1, Math.min(currentPage + 5, page.getTotalPages()));

        model.addAttribute("minPage", minPage);
        model.addAttribute("maxPage", maxPage);
        model.addAttribute("currentPage", currentPage);
    }

}
