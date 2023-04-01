package com.wordplay.unit.starter.api;

import com.wordplay.unit.starter.api.response.ResponseResult;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 统一异常处理
 *
 * @author zhuangpf
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public ResponseResult defaultErrorHandler(Exception e) {
		e.printStackTrace();
		if (e instanceof ConstraintViolationException) {
			ConstraintViolationException constraintViolationException = (ConstraintViolationException) e;
			String message = StringUtils.collectionToCommaDelimitedString(
					constraintViolationException.getConstraintViolations()
							.stream()
							.map(ConstraintViolation::getMessage)
							.collect(Collectors.toList()));
			return ResponseResult.fail(message);
		} else if (e instanceof BindException) {
			BindException bindException = (BindException) e;
			String message = StringUtils.collectionToCommaDelimitedString(
					bindException.getAllErrors()
							.stream()
							.map(DefaultMessageSourceResolvable::getDefaultMessage)
							.collect(Collectors.toList())
			);
			return ResponseResult.fail(message);
		} else if (e instanceof MethodArgumentNotValidException) {
			MethodArgumentNotValidException validException = (MethodArgumentNotValidException) e;
			String message = StringUtils.collectionToCommaDelimitedString(
					validException.getBindingResult().getAllErrors()
							.stream()
							.map(DefaultMessageSourceResolvable::getDefaultMessage)
							.collect(Collectors.toList())
			);
			return ResponseResult.fail(message);
		}
		return ResponseResult.fail(e.getMessage());
	}

}
