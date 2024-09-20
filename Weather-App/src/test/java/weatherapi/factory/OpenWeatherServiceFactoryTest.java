package weatherapi.factory;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import weatherapi.BaseTest;
import weatherapi.model.WeatherReport;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OpenWeatherServiceFactoryTest extends BaseTest {

    @Mock
    private WebClient webClient;

    @InjectMocks
    private OpenWeatherServiceFactory openWeatherServiceFactory;

    @Test
    public void getWeatherReport_WILL_returnWeatherReport_WHEN_cityAndCountryCodeExist() throws IOException {

        // Given
        String desc = "clear sky";
        initialiseMockClasses("webclient-response-city-country-code.txt");

        // Run test
        WeatherReport actual = openWeatherServiceFactory.getWeatherReport("sydney", "au");

        // Verify result
        assertNotNull(actual);
        assertNotNull(actual.getDescription());
        assertTrue(desc.equals(actual.getDescription()));
    }

    @Test
    public void getWeatherReport_WILL_returnWeatherDescription_WHEN_noCityNameExists() throws IOException {

        // Given
        String desc = "clear sky";
        initialiseMockClasses("webclient-response-no-city-name.txt");
        // Run test
        WeatherReport weatherReport = openWeatherServiceFactory.getWeatherReport("", "au");

        // Verify result
        assertNotNull(weatherReport);
        assertNotNull(weatherReport.getDescription());
        assertTrue(desc.equals(weatherReport.getDescription()));
    }

    @Test
    public void getWeatherReport_WILL_returnWeatherDescription_WHEN_noCountryCodeExists() throws IOException {

        // Given
        String desc = "overcast clouds";
        initialiseMockClasses("webclient-response-no-country-code.txt");

        // Run test
        WeatherReport weatherReport = openWeatherServiceFactory.getWeatherReport("bangkok", "");

        // Verify result
        assertNotNull(weatherReport);
        assertNotNull(weatherReport.getDescription());
        assertTrue(desc.equals(weatherReport.getDescription()));
    }


    @Test
    public void getWeatherReport_WILL_throwWebClientResponseException_WHEN_noCityNameAndNoCountryCodeExist() {

        // Given
        when(webClient.get()).thenThrow(new WebClientResponseException(123, null, null, null, null));

        // Run test
        assertThrows(WebClientResponseException.class, () -> {
            openWeatherServiceFactory.getWeatherReport("", "");
        });

    }

    @Test
    public void getWeatherReport_WILL_throwWebClientResponseException_WHEN_noCityNameAndNoCountryCodeExist1() {

        // Given
        when(webClient.get()).thenThrow(new WebClientResponseException(123, null, null, null, null));

        // Run test
        assertThrows(WebClientResponseException.class, () -> {
            openWeatherServiceFactory.getWeatherReport("", "");
        });

    }

    private void initialiseMockClasses(String filename) throws IOException {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestBodySpec requestBodySpecMock = mock(WebClient.RequestBodySpec.class);
        WebClient.ResponseSpec responseSpecMock = mock(WebClient.ResponseSpec.class);
        Mono<String> monoMock = mock(Mono.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(anyString())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.bodyToMono(String.class)).thenReturn(monoMock);
        when(monoMock.block()).thenReturn(getWebClientResponseBody(filename));
    }
    private String getWebClientResponseBody(String filename) throws IOException {
        return new String(getClass().getClassLoader().getResourceAsStream(filename).readAllBytes());
    }
}
