package space.akko.foundation.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import space.akko.foundation.common.Result;
import space.akko.foundation.common.ResultCode;
import space.akko.foundation.utils.TraceUtils;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 
 * @author akko
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常: {} - {}", e.getCode(), e.getMessage());
        return Result.<Void>error(e.getCode(), e.getMessage())
                .traceId(TraceUtils.getTraceId());
    }

    /**
     * 处理安全异常
     */
    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleSecurityException(SecurityException e, HttpServletRequest request) {
        log.warn("安全异常: {} - {}", e.getCode(), e.getMessage());
        return Result.<Void>error(e.getCode(), e.getMessage())
                .traceId(TraceUtils.getTraceId());
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数验证异常: {}", message);
        return Result.<Void>error(ResultCode.VALIDATION_ERROR, message)
                .traceId(TraceUtils.getTraceId());
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("绑定异常: {}", message);
        return Result.<Void>error(ResultCode.VALIDATION_ERROR, message)
                .traceId(TraceUtils.getTraceId());
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("约束违反异常: {}", message);
        return Result.<Void>error(ResultCode.VALIDATION_ERROR, message)
                .traceId(TraceUtils.getTraceId());
    }

    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        String message = String.format("缺少必需的请求参数: %s", e.getParameterName());
        log.warn("缺少请求参数异常: {}", message);
        return Result.<Void>error(ResultCode.BAD_REQUEST, message)
                .traceId(TraceUtils.getTraceId());
    }

    /**
     * 处理方法参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = String.format("参数类型不匹配: %s", e.getName());
        log.warn("参数类型不匹配异常: {}", message);
        return Result.<Void>error(ResultCode.BAD_REQUEST, message)
                .traceId(TraceUtils.getTraceId());
    }

    /**
     * 处理HTTP消息不可读异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("HTTP消息不可读异常: {}", e.getMessage());
        return Result.<Void>error(ResultCode.BAD_REQUEST, "请求体格式错误")
                .traceId(TraceUtils.getTraceId());
    }

    /**
     * 处理HTTP请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("HTTP请求方法不支持异常: {}", e.getMessage());
        return Result.<Void>error(ResultCode.METHOD_NOT_ALLOWED, "请求方法不支持")
                .traceId(TraceUtils.getTraceId());
    }

    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("404异常: {}", e.getMessage());
        return Result.<Void>error(ResultCode.NOT_FOUND, "请求的资源不存在")
                .traceId(TraceUtils.getTraceId());
    }

    /**
     * 处理数据完整性违反异常
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Result<Void> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.warn("数据完整性违反异常: {}", e.getMessage());
        return Result.<Void>error(ResultCode.DATA_INTEGRITY_VIOLATION, "数据完整性约束违反")
                .traceId(TraceUtils.getTraceId());
    }

    /**
     * 处理乐观锁异常
     */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Result<Void> handleOptimisticLockingFailureException(OptimisticLockingFailureException e) {
        log.warn("乐观锁异常: {}", e.getMessage());
        return Result.<Void>error(ResultCode.OPTIMISTIC_LOCK_FAILURE, "数据已被其他用户修改，请刷新后重试")
                .traceId(TraceUtils.getTraceId());
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常: ", e);
        return Result.<Void>error(ResultCode.INTERNAL_SERVER_ERROR, "系统内部错误")
                .traceId(TraceUtils.getTraceId());
    }
}
