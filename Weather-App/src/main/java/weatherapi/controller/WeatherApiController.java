package weatherapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import weatherapi.entity.ApiKeyDetail;
import weatherapi.entity.WeatherReportDetail;
import weatherapi.exception.WeatherApiCountryNameNotFoundException;
import weatherapi.exception.WeatherApiException;
import weatherapi.exception.WeatherApiInvalidQueryException;
import weatherapi.exception.WeatherApiMissingApiKeyException;
import weatherapi.factory.OpenWeatherServiceFactory;
import weatherapi.factory.WeatherReportFactory;
import weatherapi.model.ApiKey;
import weatherapi.model.WeatherReport;
import weatherapi.service.ApiKeyDetailService;
import weatherapi.service.CountryService;
import weatherapi.service.WeatherReportDaoService;
import weatherapi.utility.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

import static java.util.stream.Collectors.toList;
import static weatherapi.exception.WeatherApiError.ERROR_API_KEY_NOT_VALID;
import static weatherapi.exception.WeatherApiError.ERROR_API_KEY_REACHED_LIMIT;
import static weatherapi.exception.WeatherApiError.ERROR_COUNTRY_NAME_NOT_FOUND;
import static weatherapi.exception.WeatherApiError.ERROR_INVALID_QUERY;
import static weatherapi.exception.WeatherApiError.ERROR_MISSING_API_KEY;
import static weatherapi.exception.WeatherApiError.ERROR_MISSING_CITY_OR_COUNTRY_NAME;
import static weatherapi.utility.Utils.resetTimestampAndCallCount;
import static weatherapi.utility.Utils.validateApiKey;
import static weatherapi.utility.Utils.hasWeatherReportTimestampExpired;

@RestController
public class WeatherApiController {

    @Autowired
    private CountryService countryService;
    @Autowired
    private OpenWeatherServiceFactory openWeatherServiceFactory;
    @Autowired
    private ApiKeyDetailService apiKeyDetailService;
    @GetMapping("/weather/report")
    public WeatherReport getWeatherReport(@RequestParam(value = "q", required = true) String query,
                                          @RequestParam(value = "apiKey", required = true) String apiKeyAsString) throws WeatherApiException {

        if (StringUtils.hasText(query) && StringUtils.hasText(apiKeyAsString)) {

            StringTokenizer stringTokenizer = new StringTokenizer(query, ",");
            if (stringTokenizer.countTokens() == 2) {
                Optional<ApiKeyDetail> result = apiKeyDetailService.findByApiKey(apiKeyAsString);
                if(result.isPresent()) {
                    ApiKeyDetail apiKeyDetail = result.get();
                    apiKeyDetail = resetTimestampAndCallCount(apiKeyDetail);

                    if (validateApiKey(apiKeyDetail)) {
                        String city = stringTokenizer.nextToken().trim();
                        String country = stringTokenizer.nextToken().trim();
                        Optional<String> countryCode = countryService.findCountryCode(country);

                        if (!countryCode.isPresent()) {
                            throw new WeatherApiCountryNameNotFoundException(ERROR_COUNTRY_NAME_NOT_FOUND);
                        }
                        // Get weather report from the H2 database
                        List<WeatherReportDetail> weatherReportList = weatherReportDaoService.findByCityAndCountry(city, country);
                        WeatherReport weatherReport;

                        // If somehow there are more than one weather reports for the same city and country,
                        // delete them all and then call Open Weather Service to get a new weather report.
                        // After that save it to the H2 database.
                        if (weatherReportList.size() > 1) {
                            List<Long> ids = weatherReportList.stream().map(WeatherReportDetail::getId).collect(toList());
                            weatherReportDaoService.deleteAllById(ids);
                            weatherReport = WeatherReportFactory.getWeatherReport(openWeatherServiceFactory, city, countryCode.get());
                            saveOrUpdateWeatherReport(city, country, weatherReport.getDescription());

                        } else if (weatherReportList.size() == 1) { // One weather report exists in the H2 database
                            WeatherReportDetail existingWeatherReport = weatherReportList.get(0);
                            // If the existing weather report has not expired, return the current weather description
                            if (!hasWeatherReportTimestampExpired(existingWeatherReport.getTimestamp())) {
                                weatherReport = new WeatherReport();
                                weatherReport.setDescription(existingWeatherReport.getDescription());
                            } else { // Call the Open Weather Service to get a new weather report.
                                weatherReport = WeatherReportFactory.getWeatherReport(openWeatherServiceFactory, city, countryCode.get());
                                // Only update the existing report with the timestamp and description
                                existingWeatherReport.setTimestamp(new Date().getTime());
                                existingWeatherReport.setDescription(weatherReport.getDescription());
                                weatherReportDaoService.saveOrUpdate(existingWeatherReport);
                            }
                        } else { // else no weather report exists in the H2 database
                            weatherReport = WeatherReportFactory.getWeatherReport(openWeatherServiceFactory, city, countryCode.get());
                            saveOrUpdateWeatherReport(city, country, weatherReport.getDescription());
                        }
                        apiKeyDetail.setNumberOfTimesUsed(apiKeyDetail.getNumberOfTimesUsed() + 1);
                        apiKeyDetailService.save(apiKeyDetail);
                        return weatherReport;
                    } else {
                        apiKeyDetailService.delete(apiKeyDetail.getApiKey()); // Delete the expired API key
                        throw new WeatherApiException(ERROR_API_KEY_REACHED_LIMIT);
                    }
                } else {
                    throw new WeatherApiException(ERROR_API_KEY_NOT_VALID);
                }
            }
            throw new WeatherApiInvalidQueryException(ERROR_MISSING_CITY_OR_COUNTRY_NAME);
        }
        if (!StringUtils.hasText(query)) {
            throw new WeatherApiInvalidQueryException(ERROR_MISSING_CITY_OR_COUNTRY_NAME);
        }
        throw new WeatherApiMissingApiKeyException(ERROR_MISSING_API_KEY);
    }

    @Autowired
    private WeatherReportDaoService weatherReportDaoService;

    /**
     * Generates 5 API keys.
     * @return A list of 5 API keys.
     */
    @GetMapping("/weather/apikeys")
    public List<ApiKey> getApiKeys() {

        List<ApiKey> apiKeys = new ArrayList<>();
        for (int i=0; i < 5; i++) {
            apiKeys.add(new ApiKey(apiKeyDetailService.save(Utils.buildApiKeyDetail())));
        }
        return apiKeys;
    }

    private void saveOrUpdateWeatherReport(String city, String country, String description) {
        WeatherReportDetail weatherReportDetail = new WeatherReportDetail();
        weatherReportDetail.setTimestamp(new Date().getTime());
        weatherReportDetail.setCity(city);
        weatherReportDetail.setCountry(country);
        weatherReportDetail.setDescription(description);
        weatherReportDaoService.saveOrUpdate(weatherReportDetail);
    }

}
