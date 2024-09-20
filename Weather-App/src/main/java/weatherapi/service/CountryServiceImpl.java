package weatherapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import weatherapi.entity.Country;
import weatherapi.repository.CountryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CountryServiceImpl implements CountryService {

    private static List<Country> countries;
    @Autowired
    private CountryRepository countryDao;

    @Override
    public Optional<String> findCountryCode(String countryName) {
        if (CollectionUtils.isEmpty(countries)) {
            countries = countryDao.findAll();
        }
        Country country = countries.stream()
                                   .filter(countryEntity -> countryEntity.getCountryName().toLowerCase().equals(countryName.toLowerCase()))
                                   .findAny()
                                   .orElse(null);
        if (country != null) {
            return Optional.of(country.getCountryCode());
        }
        return Optional.empty();
    }
}
