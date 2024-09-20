package weatherapi.factory;

import weatherapi.model.WeatherReport;

public interface WeatherReportAbstractFactory {

    WeatherReport getWeatherReport(String cityName, String countryCode);
}
