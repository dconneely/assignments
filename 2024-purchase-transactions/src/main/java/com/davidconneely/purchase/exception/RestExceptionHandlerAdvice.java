package com.davidconneely.purchase.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandlerAdvice extends ResponseEntityExceptionHandler {
    private static final URI URI_ILLEGAL_ARGUMENT = URI.create("https://purchase.davidconneely.com/illegal-argument");

    // If method type is wrong, or content type is wrong, then we end up in base class.
    // If our custom exceptions (which extend ResponseStatusException) are thrown, then we end up in base class.
    // If the JSON is not valid JSON in any service, then we end up in the base class.
    // If transactionDate is an invalid date format in POST /purchase/, then we end up in the base class.

    /*
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public final ProblemDetail handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String detail = "could not read field values (check for valid JSON)";
        Throwable msc = e.getMostSpecificCause();
        if (msc instanceof DateTimeParseException) {
            detail = "could not parse date format in a field value";
        }
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setType(URI_ILLEGAL_ARGUMENT);
        return problem;
    }
    */

    // If the Jakarta-Validation fails on a Request, we end up here:
    @ExceptionHandler(ConstraintViolationException.class)
    public final ProblemDetail handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> cvs = e.getConstraintViolations();
        String detail = cvs.stream().map(cv -> (cv.getPropertyPath() + ": " + cv.getMessage())).collect(Collectors.joining("; "));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setType(URI_ILLEGAL_ARGUMENT);
        return problem;
    }

    // If the UUID is invalid in GET /purchase/{id}, we end up here:
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ProblemDetail handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String detail = e.getName() + ": could not convert field value to the expected type";
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setType(URI_ILLEGAL_ARGUMENT);
        return problem;
    }
}
