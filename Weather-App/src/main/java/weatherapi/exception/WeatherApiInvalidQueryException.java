package weatherapi.exception;

public class WeatherApiInvalidQueryException extends WeatherApiException {
    public WeatherApiInvalidQueryException(String errorMsg) {
        super(errorMsg);
    }
    public WeatherApiInvalidQueryException(Throwable throwable) {
        super(throwable);
    }
}
