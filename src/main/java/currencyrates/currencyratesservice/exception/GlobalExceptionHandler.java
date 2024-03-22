package currencyrates.currencyratesservice.exception;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Objects;
import currencyrates.currencyratesservice.dto.ApiError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {
    //region Fields
    private static final String URI = "uri=";
    private static final String EMPTY_STRING = "";
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);
    //endregion

    @ExceptionHandler(CurrencyRateNotFoundException.class)
    public ResponseEntity<ApiError> handleCurrencyRateNotFoundException(
            CurrencyRateNotFoundException ex,
            WebRequest request) {
        logger.error("Currency rate not found: ", ex);

        ApiError apiError = new ApiError(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false).replace(URI, EMPTY_STRING));

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CurrencyRateFetchException.class)
    public ResponseEntity<ApiError> handleCurrencyRateFetchException(
            CurrencyRateFetchException ex,
            WebRequest request) {
        logger.error("Currency rate fetch exception: ", ex);

        ApiError apiError = new ApiError(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                request.getDescription(false).replace(URI, EMPTY_STRING));

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CurrencyRateSaveException.class)
    public ResponseEntity<ApiError> handleCurrencyRateSaveException(
            CurrencyRateSaveException ex,
            WebRequest request) {
        logger.error("Currency rate save exception: ", ex);

        ApiError apiError = new ApiError(LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getDescription(false).replace(URI, EMPTY_STRING));

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNoSuchElementException(
            NoSuchElementException ex,
            WebRequest request) {
        logger.error("No such element exception: ", ex);

        ApiError apiError = new ApiError(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Element not found",
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {
        logger.error("Illegal argument: ", ex);

        ApiError apiError = new ApiError(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid input",
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiError> handleNullPointerException(
            NullPointerException ex,
            WebRequest request) {
        logger.error("Null pointer exception: ", ex);

        ApiError apiError = new ApiError(LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Null Pointer Exception",
                "An unexpected null value was encountered.",
                request.getDescription(false));

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            WebRequest request) {
        String message = String.format(
                "The parameter '%s' of value '%s' could not be converted to type '%s'",
                ex.getName(),
                ex.getValue(),
                Objects.requireNonNull(ex.getRequiredType()).getSimpleName());

        ApiError apiError = new ApiError(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Method Argument Type Mismatch",
                message,
                request.getDescription(false));

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(
            Exception ex,
            WebRequest request) {
        ApiError apiError = new ApiError(LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
