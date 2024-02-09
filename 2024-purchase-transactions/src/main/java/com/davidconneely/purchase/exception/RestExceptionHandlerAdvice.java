package com.davidconneely.purchase.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.format.DateTimeParseException;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandlerAdvice extends ResponseEntityExceptionHandler {
    private static final URI URI_ILLEGAL_ARGUMENT = URI.create("https://purchase.davidconneely.com/illegal-argument");

    // If the JSON is not valid JSON in any service, then we end up here:
    // If transactionDate is an invalid date format in POST /purchase/, then we end up here:
    // Override the detail message and the type.
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String detail = "could not read field values (check for valid JSON)";
        Throwable msc = ex.getMostSpecificCause();
        if (msc instanceof DateTimeParseException) {
            detail = "could not parse date format in a field value";
        }
        ProblemDetail body = createProblemDetail(ex, status, detail, null, null, request);
        body.setType(URI_ILLEGAL_ARGUMENT);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    // If the UUID is invalid in GET /purchase/{id}, we end up here:
    // Override the detail message and the type.
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String detail = ex.getPropertyName() + ": could not convert field value '" + ex.getValue() + "' to expected type";
        ProblemDetail body = createProblemDetail(ex, status, detail, null, null, request);
        body.setType(URI_ILLEGAL_ARGUMENT);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    // If the Jakarta-Validation fails on a Request, we end up here:
    @ExceptionHandler(ConstraintViolationException.class)
    public final ProblemDetail handleConstraintViolation(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> cvs = e.getConstraintViolations();
        String detail = cvs.stream().map(cv -> (cv.getPropertyPath() + ": " + cv.getMessage())).collect(Collectors.joining("; "));
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problem.setType(URI_ILLEGAL_ARGUMENT);
        return problem;
    }

    // If HTTP method type is wrong, or content type header is wrong, then we end up in base class.
    // If our custom exceptions (which extend ResponseStatusException) are thrown, then we end up in base class.
}
