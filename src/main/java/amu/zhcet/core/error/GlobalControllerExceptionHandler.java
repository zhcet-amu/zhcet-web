package amu.zhcet.core.error;

import amu.zhcet.data.ItemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
class GlobalControllerExceptionHandler {

    private String getNotFoundTitle(ItemNotFoundException infe) {
        if (infe.getId() == null)
            return infe.getType() + " Not Found";
        return String.format("%s %s Not Found", infe.getType(), infe.getId());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ItemNotFoundException.class)
    public String handleNotFound(Model model, ItemNotFoundException infe) {
        model.addAttribute("page_title", getNotFoundTitle(infe));
        model.addAttribute("type", infe.getType());
        model.addAttribute("id", infe.getId());
        return "not_found";
    }

}
