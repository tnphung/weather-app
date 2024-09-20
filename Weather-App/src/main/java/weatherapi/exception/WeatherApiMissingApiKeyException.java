package weatherapi.exception;

public class WeatherApiMissingApiKeyException extends WeatherApiException{
    public WeatherApiMissingApiKeyException(String errorMsg) {
        super(errorMsg);
    }
    public WeatherApiMissingApiKeyException(Throwable throwable) {
        super(throwable);
    }
}
