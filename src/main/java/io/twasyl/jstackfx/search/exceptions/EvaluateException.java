package io.twasyl.jstackfx.search.exceptions;

/**
 * @author Thierry Wasylczenko
 * @since JStackFX 1.1
 */
public class EvaluateException extends Exception {
    public EvaluateException() {
    }

    public EvaluateException(String message) {
        super(message);
    }

    public EvaluateException(String message, Throwable cause) {
        super(message, cause);
    }

    public EvaluateException(Throwable cause) {
        super(cause);
    }

    public EvaluateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
