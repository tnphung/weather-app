package weatherapi.service;

import java.util.Optional;

public interface CountryService {

    Optional<String> findCountryCode(String countryName);
}
