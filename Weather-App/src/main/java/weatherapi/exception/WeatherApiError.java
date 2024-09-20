package weatherapi.exception;

public interface WeatherApiError {

    String ERROR_INVALID_QUERY = "Invalid query";
    String  ERROR_MISSING_CITY_OR_COUNTRY_NAME = "Missing city and/or country name";
    String ERROR_COUNTRY_NAME_NOT_FOUND = "Country name is not found";
    String ERROR_CITY_NAME_NOT_FOUND = "City name is not found";
    String ERROR_API_KEY_REACHED_LIMIT = "The API key has reached its limit";
    String ERROR_API_KEY_NOT_VALID = "The API key is not valid!" ;
    String ERROR_MISSING_API_KEY = "Missing API key";

}
