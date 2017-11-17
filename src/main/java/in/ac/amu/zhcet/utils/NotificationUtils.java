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

    private static int getSafePage(int page, int total) {
        return Math.max(1, Math.min(page, total));
    }

    public static void prepareNotifications(Model model, Page<?> page, int currentPage) {
        int totalPages = page.getTotalPages();
        int minPage = getSafePage(currentPage - 5, totalPages);
        int maxPage = getSafePage(currentPage + 5, totalPages);

        model.addAttribute("minPage", minPage);
        model.addAttribute("maxPage", maxPage);
        model.addAttribute("currentPage", currentPage);
    }

}
