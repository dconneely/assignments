package com.davidconneely.purchase.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.time.format.DateTimeParseException;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandlerAdvice {
    private static final URI URI_ILLEGAL_ARGUMENT = URI.create("https://purchase.davidconneely.com/illegal-argument");

    // If POST to /purchase/{id} or GET from /purchase/, we end up here:
    // If our custom exceptions (extending ResponseStatusException) are thrown, we end up here:
    @ExceptionHandler({ErrorResponseException.class, HttpRequestMethodNotSupportedException.class})
    public final ProblemDetail handleErrorResponse(ErrorResponse e) {
        return e.getBody();
    }

    // If the Jakarta-Validation fails on a Request, we end up here:
    @ExceptionHandler(ConstraintViolationException.class)
    public final ProblemDetail handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> cvs = e.getConstraintViolations();
        String detail = cvs.stream().map(cv -> (cv.getPropertyPath() + ": " + cv.getMessage())).collect(Collectors.joining("; "));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setType(URI_ILLEGAL_ARGUMENT);
        return problem;
    }

    // If the JSON is not valid JSON in any service, we end up here:,
    // If transactionDate is an invalid date format in POST /purchase/, we end up here:
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

    // If the UUID is invalid in GET /purchase/{id}, we end up here:
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public final ProblemDetail handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String detail = e.getName() + ": could not convert field value to the expected type";
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setType(URI_ILLEGAL_ARGUMENT);
        return problem;
    }
}
