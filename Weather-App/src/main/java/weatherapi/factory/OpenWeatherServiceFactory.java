package weatherapi.factory;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import weatherapi.model.WeatherReport;

@Service
public class OpenWeatherServiceFactory implements WeatherReportAbstractFactory {

    @Autowired
    private WebClient webClient;

    @Value("${app.id}")
    private String appId;

    @Override
    public WeatherReport getWeatherReport(String cityName, String countryCode) {

        String response = webClient.get()
                                   .uri("?q=" + cityName + "," + countryCode + "&appid=" + appId)
                                   .retrieve().bodyToMono(String.class).block();

        WeatherReport weatherReport = getWeatherReport(response);
        return weatherReport;

    }

    /**
     * Gets the weather description from the API response using JsonParser class.
     * @param response
     * @return
     */
    private WeatherReport getWeatherReport(String response) {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(response);
        JsonElement weatherElement = jsonElement.getAsJsonObject().get("weather");
        JsonElement descParser = weatherElement.getAsJsonArray().get(0);
        String description = descParser.getAsJsonObject().get("description").toString().replace("\"", "");
        WeatherReport weatherReport = new WeatherReport();
        weatherReport.setDescription(description);
        return weatherReport;
    }
}
