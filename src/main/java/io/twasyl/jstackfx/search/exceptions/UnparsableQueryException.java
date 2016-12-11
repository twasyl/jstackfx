package io.twasyl.jstackfx.search.exceptions;

/**
 * @author Thierry Wasylczenko
 * @since JStackFX 1.1
 */
public class UnparsableQueryException extends Exception {
    public UnparsableQueryException() {
    }

    public UnparsableQueryException(String message) {
        super(message);
    }

    public UnparsableQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnparsableQueryException(Throwable cause) {
        super(cause);
    }

    public UnparsableQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
