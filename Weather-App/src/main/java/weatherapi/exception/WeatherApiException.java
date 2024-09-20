package weatherapi.exception;

public class WeatherApiException extends Exception {

    public WeatherApiException(String errorMsg) {
        super(errorMsg);
    }
    public WeatherApiException(Throwable throwable) {
        super(throwable);
    }
}
