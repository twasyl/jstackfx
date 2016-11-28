package io.twasyl.jstackfx.exceptions;

/**
 * Main exception when working with {@link io.twasyl.jstackfx.beans.Dump}.
 *
 * @author Thierry Wasylczenko
 * @since JStackFX 1.0
 */
public class DumpException extends Exception {

    public DumpException() {
    }

    public DumpException(String message) {
        super(message);
    }

    public DumpException(String message, Throwable cause) {
        super(message, cause);
    }

    public DumpException(Throwable cause) {
        super(cause);
    }

    public DumpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
