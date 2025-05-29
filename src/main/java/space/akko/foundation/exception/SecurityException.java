package space.akko.foundation.exception;

import space.akko.foundation.common.ResultCode;

/**
 * 安全异常
 * 
 * @author akko
 * @since 1.0.0
 */
public class SecurityException extends BaseException {

    private static final long serialVersionUID = 1L;

    public SecurityException(String message) {
        super(ResultCode.UNAUTHORIZED, message);
    }

    public SecurityException(ResultCode resultCode) {
        super(resultCode);
    }

    public SecurityException(ResultCode resultCode, String message) {
        super(resultCode, message);
    }

    public SecurityException(Integer code, String message) {
        super(code, message);
    }

    public SecurityException(String message, Throwable cause) {
        super(ResultCode.UNAUTHORIZED.getCode(), message, cause);
    }

    public SecurityException(ResultCode resultCode, Throwable cause) {
        super(resultCode, cause);
    }
}
