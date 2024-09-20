package weatherapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static weatherapi.exception.WeatherApiError.ERROR_CITY_NAME_NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({WeatherApiInvalidQueryException.class,
            WeatherApiMissingApiKeyException.class,
            MissingServletRequestParameterException.class})
    public final ErrorResponse handleBadRequestException(Exception ex) {
        if (ex instanceof MissingServletRequestParameterException) {
            return ErrorResponse.create(ex, HttpStatus.BAD_REQUEST,
                                        ((MissingServletRequestParameterException) ex).getBody().getDetail());
        }
        return ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, ex.getLocalizedMessage());
    }

    @ExceptionHandler({WeatherApiCountryNameNotFoundException.class})
    public final ErrorResponse handleNotFoundException(WeatherApiCountryNameNotFoundException ex) {
        return ErrorResponse.create(ex, HttpStatus.NOT_FOUND, ex.getLocalizedMessage());
    }

    @ExceptionHandler({WeatherApiException.class})
    public final ErrorResponse handleServiceUnavailableException(WeatherApiException ex) {
        return ErrorResponse.create(ex, HttpStatus.SERVICE_UNAVAILABLE, ex.getLocalizedMessage());

    }
    @ExceptionHandler({WebClientResponseException.class})
    public final ErrorResponse handleWebClientResponseException(WebClientResponseException ex) {
        if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
            return ErrorResponse.create(ex, ex.getStatusCode(), ERROR_CITY_NAME_NOT_FOUND);
        }
        return ErrorResponse.create(ex, ex.getStatusCode(), ex.getLocalizedMessage());
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public final ErrorResponse handleAllExceptions(Exception ex) {
        return ErrorResponse.create(ex, HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage());
    }
}
