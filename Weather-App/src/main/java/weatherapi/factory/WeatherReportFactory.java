package weatherapi.factory;

import weatherapi.model.WeatherReport;

public class WeatherReportFactory {

    public static WeatherReport getWeatherReport(WeatherReportAbstractFactory weatherReportFactory, String cityName, String countryCode) {
        return weatherReportFactory.getWeatherReport(cityName, countryCode);
    }
}
