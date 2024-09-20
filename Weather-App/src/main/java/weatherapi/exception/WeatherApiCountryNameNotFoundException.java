package weatherapi.exception;

public class WeatherApiCountryNameNotFoundException extends WeatherApiException {

    public WeatherApiCountryNameNotFoundException(String errorMsg) {
        super(errorMsg);
    }
    public WeatherApiCountryNameNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
