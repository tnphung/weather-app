package weatherapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import weatherapi.BaseTest;
import weatherapi.entity.Country;
import weatherapi.repository.CountryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CountryServiceTest extends BaseTest {

    @Mock
    private CountryRepository countryRepositoryMock;
    @InjectMocks
    private CountryServiceImpl countryService;

    @BeforeEach
    private void setup() {
        ReflectionTestUtils.setField(CountryServiceImpl.class, "countries", null);
    }
    @Test
    public void findCountryCode_WILL_returnCountryCode_WHEN_validCountryNameExists() {

        // Given
        List<Country> countries = createCountryList();
        when(countryRepositoryMock.findAll()).thenReturn(countries);

        // Run test
        Optional<String> countryCode = countryService.findCountryCode("country-name-5");

        // Verify result
        assertNotNull(countryCode);
        assertTrue(countryCode.isPresent());
        assertEquals(countryCode.get(), "country-code-5");
        verify(countryRepositoryMock, times(1)).findAll();
    }

    @Test
    public void findCountryCode_WILL_returnNothing_WHEN_invalidCountryNameExists() {

        // Given
        List<Country> countries = createCountryList();
        when(countryRepositoryMock.findAll()).thenReturn(countries);

        // Run test
        Optional<String> countryCode = countryService.findCountryCode("abcde");

        // Verify result
        assertNotNull(countryCode);
        assertTrue(countryCode.isEmpty());
        verify(countryRepositoryMock, times(1)).findAll();
    }

    @Test
    public void findCountryCode_WILL_notCallMethodFindAll_WHEN_countryListAlreadyExists() {

        // Given
        List<Country> countries = createCountryList();
        ReflectionTestUtils.setField(CountryServiceImpl.class, "countries", countries);

        // Run test
        Optional<String> countryCode = countryService.findCountryCode("abcde");

        // Verify result
        assertNotNull(countryCode);
        assertTrue(countryCode.isEmpty());
        verify(countryRepositoryMock, times(0)).findAll();
    }

    private static List<Country> createCountryList() {
        List<Country> countries = new ArrayList<>();
        Country country;
        for (int i=0; i < 10; i++) {
            country = new Country();
            country.setId(100+i);
            country.setCountryName("country-name-"+i);
            country.setCountryCode("country-code-"+i);
            countries.add(country);
        }
        return countries;
    }
}
