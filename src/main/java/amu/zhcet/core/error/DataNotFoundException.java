package amu.zhcet.core.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * To be thrown only if there is no specialized error page to be shown
 */
@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such Item")
public class DataNotFoundException extends RuntimeException {
}
