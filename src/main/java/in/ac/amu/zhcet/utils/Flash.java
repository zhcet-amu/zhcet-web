package in.ac.amu.zhcet.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Flash {

    private String title;
    private final String message;
    private FlashType type = FlashType.SUCCESS;

    public Flash(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public static class FlashBuilder {
        private String title;

        FlashBuilder(String title) {
            this.title = title;
        }

        public Flash success(String message) {
            Flash flash = Flash.success(message);
            flash.title = title;
            return flash;
        }

        public Flash warning(String message) {
            Flash flash = Flash.warning(message);
            flash.title = title;
            return flash;
        }

        public Flash error(String message) {
            Flash flash = Flash.error(message);
            flash.title = title;
            return flash;
        }

        public Flash info(String message) {
            Flash flash = Flash.info(message);
            flash.title = title;
            return flash;
        }
    }

    public static FlashBuilder title(String title) {
        return new FlashBuilder(title);
    }

    public static Flash success(String message) {
        Flash flash = new Flash(message);
        flash.type = FlashType.SUCCESS;
        return flash;
    }

    public static Flash error(String message) {
        Flash flash = new Flash(message);
        flash.type = FlashType.ERROR;
        return flash;
    }

    public static Flash warning(String message) {
        Flash flash = new Flash(message);
        flash.type = FlashType.WARNING;
        return flash;
    }

    public static Flash info(String message) {
        Flash flash = new Flash(message);
        flash.type = FlashType.INFO;
        return flash;
    }

    // CSS Class
    public String getCss() {
        switch (type) {
            case SUCCESS:
                return "success";
            case ERROR:
                return "danger";
            case WARNING:
                return "warning";
            case INFO:
                return "info";
            default:
                return "success";
        }
    }

}
