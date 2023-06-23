package me.jwtPractice.tutorial.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.List;
import me.jwtPractice.tutorial.dto.ErrorDto;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Order(Ordered.HIGHEST_PRECEDENCE) // 다른 Handler 혹은 Interceptor 보다 먼저 수행되도록 순서 지정
@ControllerAdvice
public class MethodArgumentNotValidExceptionHandler {

    // MethodArgumentNotValidException이 발생했을 때 호출되는 메소드
    // HTTP 상태 코드를 BAD_REQUEST(400)로 설정하고, ErrorDto를 반환
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorDto methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<org.springframework.validation.FieldError> fieldErrors = result.getFieldErrors();
        return processFieldErrors(fieldErrors);
    }

    // fieldErrors를 처리하여 ErrorDto를 반환하는 메소드
    private ErrorDto processFieldErrors(List<org.springframework.validation.FieldError> fieldErrors) {
        ErrorDto errorDTO = new ErrorDto(BAD_REQUEST.value(), "@Valid Error");
        for (org.springframework.validation.FieldError fieldError: fieldErrors) {
            errorDTO.addFieldError(fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage());
        }
        return errorDTO;
    }
}
