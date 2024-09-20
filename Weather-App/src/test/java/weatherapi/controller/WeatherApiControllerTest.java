package weatherapi.controller;


import com.google.gson.Gson;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import weatherapi.BaseTest;
import weatherapi.entity.ApiKeyDetail;
import weatherapi.entity.WeatherReportDetail;
import weatherapi.factory.OpenWeatherServiceFactory;
import weatherapi.factory.WeatherReportFactory;
import weatherapi.model.WeatherReport;
import weatherapi.service.ApiKeyDetailService;
import weatherapi.service.CountryService;
import weatherapi.service.WeatherReportDaoService;
import weatherapi.utility.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static weatherapi.exception.WeatherApiError.ERROR_API_KEY_NOT_VALID;
import static weatherapi.exception.WeatherApiError.ERROR_API_KEY_REACHED_LIMIT;
import static weatherapi.exception.WeatherApiError.ERROR_INVALID_QUERY;
import static weatherapi.exception.WeatherApiError.ERROR_MISSING_API_KEY;
import static weatherapi.exception.WeatherApiError.ERROR_MISSING_CITY_OR_COUNTRY_NAME;
import static weatherapi.utility.Utils.ONE_HOUR;
import static weatherapi.utility.Utils.capitaliseString;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = WeatherApiController.class)
public class WeatherApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryService countryServiceMock;
    @MockBean
    private OpenWeatherServiceFactory openWeatherServiceFactoryMock;
    @MockBean
    private ApiKeyDetailService apiKeyDetailServiceMock;
    @MockBean
    private WeatherReportDaoService weatherReportDaoServiceMock;

    private String city = "sydney";
    private String country = "australia";

    @Test
    public void getWeatherReport_WILL_returnWeatherDescription_WHEN_thereIsNoExistingWeatherReport() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        Optional<ApiKeyDetail> apiKeyDetail = Optional.of(Utils.buildApiKeyDetail());

        when(apiKeyDetailServiceMock.findByApiKey(anyString())).thenReturn(apiKeyDetail);
        when(countryServiceMock.findCountryCode(anyString())).thenReturn(Optional.of("au"));
        when(weatherReportDaoServiceMock.findByCityAndCountry(anyString(), anyString())).thenReturn(new ArrayList<>());
        WeatherReport weatherReport = new WeatherReport();
        weatherReport.setDescription("clear sky");
        when(openWeatherServiceFactoryMock.getWeatherReport(anyString(), anyString())).thenReturn(weatherReport);
        when(weatherReportDaoServiceMock.saveOrUpdate(any(WeatherReportDetail.class))).thenReturn(new WeatherReportDetail());
        when(apiKeyDetailServiceMock.save(any(ApiKeyDetail.class))).thenReturn(new ApiKeyDetail());

        // Run test
        StringBuilder urlBuilder = new StringBuilder("/weather/report?q=");
        urlBuilder.append(city).append(",").append(country).append("&apiKey=dce03eae01aa390619209140981");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                                   .get(urlBuilder.toString())
                                                   .accept(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isOk()).andReturn();

        // Verify
        assertNotNull(result);
        assertNotNull(result.getResponse());
        Gson gson = new Gson();
        String expected = gson.toJson(weatherReport);
        assertTrue(expected.equals(result.getResponse().getContentAsString()));

        verify(apiKeyDetailServiceMock, times(1)).findByApiKey(anyString());
        verify(countryServiceMock, times(1)).findCountryCode(anyString());
        verify(weatherReportDaoServiceMock, times(1)).findByCityAndCountry(anyString(), anyString());
        verify(openWeatherServiceFactoryMock, times(1)).getWeatherReport(anyString(), anyString());
        verify(weatherReportDaoServiceMock, times(1)).saveOrUpdate(any(WeatherReportDetail.class));
        verify(apiKeyDetailServiceMock, times(1)).save(any(ApiKeyDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).delete(anyString());
        verify(weatherReportDaoServiceMock, times(0)).deleteAllById(any(List.class));
    }

    @Test
    public void getWeatherReport_WILL_returnWeatherDescription_WHEN_thereIsMoreThanOneExistingWeatherReport() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        Optional<ApiKeyDetail> apiKeyDetail = Optional.of(Utils.buildApiKeyDetail());

        when(apiKeyDetailServiceMock.findByApiKey(anyString())).thenReturn(apiKeyDetail);
        when(countryServiceMock.findCountryCode(anyString())).thenReturn(Optional.of("au"));
        // Set multiple existing weather reports
        List<WeatherReportDetail> weatherReportList = new ArrayList<>();
        weatherReportList.add(buildWeatherReportDetail("sydney", "australia"));
        weatherReportList.add(buildWeatherReportDetail("sydney", "australia"));
        when(weatherReportDaoServiceMock.findByCityAndCountry(anyString(), anyString())).thenReturn(weatherReportList);
        doNothing().when(weatherReportDaoServiceMock).deleteAllById(any(List.class));

        WeatherReport weatherReport = new WeatherReport();
        weatherReport.setDescription("clear sky");
        when(openWeatherServiceFactoryMock.getWeatherReport(anyString(), anyString())).thenReturn(weatherReport);
        when(weatherReportDaoServiceMock.saveOrUpdate(any(WeatherReportDetail.class))).thenReturn(new WeatherReportDetail());
        when(apiKeyDetailServiceMock.save(any(ApiKeyDetail.class))).thenReturn(new ApiKeyDetail());

        // Run test
        StringBuilder urlBuilder = new StringBuilder("/weather/report?q=");
        urlBuilder.append(city).append(",").append(country).append("&apiKey=dce03eae01aa390619209140981");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                                   .get(urlBuilder.toString())
                                                   .accept(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isOk()).andReturn();

        // Verify
        assertNotNull(result);
        assertNotNull(result.getResponse());

        Gson gson = new Gson();
        String expected = gson.toJson(weatherReport);
        assertTrue(expected.equals(result.getResponse().getContentAsString()));

        verify(apiKeyDetailServiceMock, times(1)).findByApiKey(anyString());
        verify(countryServiceMock, times(1)).findCountryCode(anyString());
        verify(weatherReportDaoServiceMock, times(1)).findByCityAndCountry(anyString(), anyString());
        verify(weatherReportDaoServiceMock, times(1)).deleteAllById(any(List.class));
        verify(openWeatherServiceFactoryMock, times(1)).getWeatherReport(anyString(), anyString());
        verify(weatherReportDaoServiceMock, times(1)).saveOrUpdate(any(WeatherReportDetail.class));
        verify(apiKeyDetailServiceMock, times(1)).save(any(ApiKeyDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).delete(anyString());
    }

    @Test
    public void getWeatherReport_WILL_returnWeatherDescription_WHEN_thereIsOneExistingWeatherReport() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(apiKeyDetailServiceMock.findByApiKey(anyString())).thenReturn(Optional.of(Utils.buildApiKeyDetail()));
        when(countryServiceMock.findCountryCode(anyString())).thenReturn(Optional.of("au"));

        // Set one existing weather report
        List<WeatherReportDetail> weatherReportList = new ArrayList<>();
        weatherReportList.add(buildWeatherReportDetail("sydney", "australia"));
        when(weatherReportDaoServiceMock.findByCityAndCountry(anyString(), anyString())).thenReturn(weatherReportList);
        when(apiKeyDetailServiceMock.save(any(ApiKeyDetail.class))).thenReturn(new ApiKeyDetail());

        // Run test
        StringBuilder urlBuilder = new StringBuilder("/weather/report?q=");
        urlBuilder.append(city).append(",").append(country).append("&apiKey=dce03eae01aa390619209140981");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                                   .get(urlBuilder.toString())
                                                   .accept(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isOk()).andReturn();

        // Verify
        assertNotNull(result);
        assertNotNull(result.getResponse());
        String expected = "{\"description\":\"This is a unit test\"}";
        assertTrue(expected.equals(result.getResponse().getContentAsString()));

        verify(apiKeyDetailServiceMock, times(1)).findByApiKey(anyString());
        verify(countryServiceMock, times(1)).findCountryCode(anyString());
        verify(weatherReportDaoServiceMock, times(1)).findByCityAndCountry(anyString(), anyString());
        verify(openWeatherServiceFactoryMock, times(0)).getWeatherReport(anyString(), anyString());
        verify(weatherReportDaoServiceMock, times(0)).saveOrUpdate(any(WeatherReportDetail.class));
        verify(apiKeyDetailServiceMock, times(1)).save(any(ApiKeyDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).delete(anyString());
        verify(weatherReportDaoServiceMock, times(0)).deleteAllById(any(List.class));
    }

    @Test
    public void getWeatherReport_WILL_returnWeatherDescription_WHEN_theExistingWeatherReportHasExpired() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(apiKeyDetailServiceMock.findByApiKey(anyString())).thenReturn(Optional.of(Utils.buildApiKeyDetail()));
        when(countryServiceMock.findCountryCode(anyString())).thenReturn(Optional.of("au"));

        // Set one existing weather report which has expired.
        List<WeatherReportDetail> weatherReportList = new ArrayList<>();
        WeatherReportDetail weatherReportDetail = buildWeatherReportDetail("sydney", "australia");
        weatherReportDetail.setTimestamp(weatherReportDetail.getTimestamp() - ONE_HOUR - 5);
        weatherReportList.add(weatherReportDetail);

        when(weatherReportDaoServiceMock.findByCityAndCountry(anyString(), anyString())).thenReturn(weatherReportList);
        WeatherReport weatherReport = new WeatherReport();
        weatherReport.setDescription("clear sky");
        when(openWeatherServiceFactoryMock.getWeatherReport(anyString(), anyString())).thenReturn(weatherReport);
        when(apiKeyDetailServiceMock.save(any(ApiKeyDetail.class))).thenReturn(new ApiKeyDetail());

        // Run test
        StringBuilder urlBuilder = new StringBuilder("/weather/report?q=");
        urlBuilder.append(city).append(",").append(country).append("&apiKey=dce03eae01aa390619209140981");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                                   .get(urlBuilder.toString())
                                                   .accept(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isOk()).andReturn();

        // Verify
        assertNotNull(result);
        assertNotNull(result.getResponse());
        Gson gson = new Gson();
        String expected = gson.toJson(weatherReport);
        assertTrue(expected.equals(result.getResponse().getContentAsString()));

        verify(apiKeyDetailServiceMock, times(1)).findByApiKey(anyString());
        verify(countryServiceMock, times(1)).findCountryCode(anyString());
        verify(weatherReportDaoServiceMock, times(1)).findByCityAndCountry(anyString(), anyString());
        verify(openWeatherServiceFactoryMock, times(1)).getWeatherReport(anyString(), anyString());
        verify(weatherReportDaoServiceMock, times(1)).saveOrUpdate(any(WeatherReportDetail.class));
        verify(apiKeyDetailServiceMock, times(1)).save(any(ApiKeyDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).delete(anyString());
        verify(weatherReportDaoServiceMock, times(0)).deleteAllById(any(List.class));
    }

    @Test
    public void getWeatherReport_WILL_returnServiceUnavailableError_WHEN_apiKeyHasReachedLimit() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        ApiKeyDetail apiKeyDetail = Utils.buildApiKeyDetail();
        apiKeyDetail.setNumberOfTimesUsed(5);
        when(apiKeyDetailServiceMock.findByApiKey(anyString())).thenReturn(Optional.of(apiKeyDetail));
        doNothing().when(apiKeyDetailServiceMock).delete(anyString());

        // Run test
        StringBuilder urlBuilder = new StringBuilder("/weather/report?q=");
        urlBuilder.append(city).append(",").append(country).append("&apiKey=dce03eae01aa390619209140981");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                                   .get(urlBuilder.toString())
                                                   .accept(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isServiceUnavailable()).andReturn();

        // Verify
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains(ERROR_API_KEY_REACHED_LIMIT));

        verify(apiKeyDetailServiceMock, times(1)).findByApiKey(anyString());
        verify(countryServiceMock, times(0)).findCountryCode(anyString());
        verify(weatherReportDaoServiceMock, times(0)).findByCityAndCountry(anyString(), anyString());
        verify(openWeatherServiceFactoryMock, times(0)).getWeatherReport(anyString(), anyString());
        verify(weatherReportDaoServiceMock, times(0)).saveOrUpdate(any(WeatherReportDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).save(any(ApiKeyDetail.class));
        verify(apiKeyDetailServiceMock, times(1)).delete(anyString());
        verify(weatherReportDaoServiceMock, times(0)).deleteAllById(any(List.class));
    }

    @Test
    public void getWeatherReport_WILL_returnServiceUnavailableError_WHEN_apiKeyNotValid() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(apiKeyDetailServiceMock.findByApiKey(anyString())).thenReturn(Optional.empty());

        // Run test
        StringBuilder urlBuilder = new StringBuilder("/weather/report?q=");
        urlBuilder.append(city).append(",").append(country).append("&apiKey=dce03eae01aa390619209140981");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                                   .get(urlBuilder.toString())
                                                   .accept(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isServiceUnavailable()).andReturn();

        // Verify
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains(ERROR_API_KEY_NOT_VALID));

        verify(apiKeyDetailServiceMock, times(1)).findByApiKey(anyString());
        verify(countryServiceMock, times(0)).findCountryCode(anyString());
        verify(weatherReportDaoServiceMock, times(0)).findByCityAndCountry(anyString(), anyString());
        verify(openWeatherServiceFactoryMock, times(0)).getWeatherReport(anyString(), anyString());
        verify(weatherReportDaoServiceMock, times(0)).saveOrUpdate(any(WeatherReportDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).save(any(ApiKeyDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).delete(anyString());
        verify(weatherReportDaoServiceMock, times(0)).deleteAllById(any(List.class));
    }

    @Test
    public void getWeatherReport_WILL_returnBadRequestError_WHEN_countryNameDoesNotExist() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(apiKeyDetailServiceMock.findByApiKey(anyString())).thenReturn(Optional.empty());

        // Run test
        StringBuilder urlBuilder = new StringBuilder("/weather/report?q=");
        urlBuilder.append(city).append(",").append("&apiKey=dce03eae01aa390619209140981");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                                   .get(urlBuilder.toString())
                                                   .accept(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isBadRequest()).andReturn();

        // Verify
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains(ERROR_MISSING_CITY_OR_COUNTRY_NAME));

        verify(apiKeyDetailServiceMock, times(0)).findByApiKey(anyString());
        verify(countryServiceMock, times(0)).findCountryCode(anyString());
        verify(weatherReportDaoServiceMock, times(0)).findByCityAndCountry(anyString(), anyString());
        verify(openWeatherServiceFactoryMock, times(0)).getWeatherReport(anyString(), anyString());
        verify(weatherReportDaoServiceMock, times(0)).saveOrUpdate(any(WeatherReportDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).save(any(ApiKeyDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).delete(anyString());
        verify(weatherReportDaoServiceMock, times(0)).deleteAllById(any(List.class));
    }

    @Test
    public void getWeatherReport_WILL_returnBadRequestError_WHEN_cityNameDoesNotExist() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(apiKeyDetailServiceMock.findByApiKey(anyString())).thenReturn(Optional.empty());

        // Run test
        StringBuilder urlBuilder = new StringBuilder("/weather/report?q=");
        urlBuilder.append(",").append(country).append("&apiKey=dce03eae01aa390619209140981");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                                   .get(urlBuilder.toString())
                                                   .accept(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isBadRequest()).andReturn();

        // Verify
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains(ERROR_MISSING_CITY_OR_COUNTRY_NAME));

        verify(apiKeyDetailServiceMock, times(0)).findByApiKey(anyString());
        verify(countryServiceMock, times(0)).findCountryCode(anyString());
        verify(weatherReportDaoServiceMock, times(0)).findByCityAndCountry(anyString(), anyString());
        verify(openWeatherServiceFactoryMock, times(0)).getWeatherReport(anyString(), anyString());
        verify(weatherReportDaoServiceMock, times(0)).saveOrUpdate(any(WeatherReportDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).save(any(ApiKeyDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).delete(anyString());
        verify(weatherReportDaoServiceMock, times(0)).deleteAllById(any(List.class));
    }

    @Test
    public void getWeatherReport_WILL_returnBadRequestError_WHEN_bothCityAndCountryNameDoNotExist() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(apiKeyDetailServiceMock.findByApiKey(anyString())).thenReturn(Optional.empty());
        city = "";
        country = "";

        // Run test
        StringBuilder urlBuilder = new StringBuilder("/weather/report?");
        urlBuilder.append("q=").append(city).append(",").append(country).append("&apiKey=dce03eae01aa390619209140981");


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                                   .get(urlBuilder.toString())
                                                   .accept(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isBadRequest()).andReturn();

        // Verify
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains(ERROR_MISSING_CITY_OR_COUNTRY_NAME));

        verify(apiKeyDetailServiceMock, times(0)).findByApiKey(anyString());
        verify(countryServiceMock, times(0)).findCountryCode(anyString());
        verify(weatherReportDaoServiceMock, times(0)).findByCityAndCountry(anyString(), anyString());
        verify(openWeatherServiceFactoryMock, times(0)).getWeatherReport(anyString(), anyString());
        verify(weatherReportDaoServiceMock, times(0)).saveOrUpdate(any(WeatherReportDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).save(any(ApiKeyDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).delete(anyString());
        verify(weatherReportDaoServiceMock, times(0)).deleteAllById(any(List.class));
    }

    @Test
    public void getWeatherReport_WILL_returnBadRequestError_WHEN_queryDoesNotExist() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(apiKeyDetailServiceMock.findByApiKey(anyString())).thenReturn(Optional.empty());

        // Run test
        StringBuilder urlBuilder = new StringBuilder("/weather/report?");
        urlBuilder.append("q=").append("&apiKey=dce03eae01aa390619209140981");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                                   .get(urlBuilder.toString())
                                                   .accept(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isBadRequest()).andReturn();

        // Verify
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains(ERROR_MISSING_CITY_OR_COUNTRY_NAME));

        verify(apiKeyDetailServiceMock, times(0)).findByApiKey(anyString());
        verify(countryServiceMock, times(0)).findCountryCode(anyString());
        verify(weatherReportDaoServiceMock, times(0)).findByCityAndCountry(anyString(), anyString());
        verify(openWeatherServiceFactoryMock, times(0)).getWeatherReport(anyString(), anyString());
        verify(weatherReportDaoServiceMock, times(0)).saveOrUpdate(any(WeatherReportDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).save(any(ApiKeyDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).delete(anyString());
        verify(weatherReportDaoServiceMock, times(0)).deleteAllById(any(List.class));
    }

    @Test
    public void getWeatherReport_WILL_returnBadRequestError_WHEN_apiKeyDoesNotExist() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(apiKeyDetailServiceMock.findByApiKey(anyString())).thenReturn(Optional.empty());

        // Run test
        StringBuilder urlBuilder = new StringBuilder("/weather/report?");
        urlBuilder.append("q=").append(city).append(",").append(country).append("&apiKey=");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                                   .get(urlBuilder.toString())
                                                   .accept(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isBadRequest()).andReturn();

        // Verify
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
        assertTrue(result.getResponse().getContentAsString().contains(ERROR_MISSING_API_KEY));

        verify(apiKeyDetailServiceMock, times(0)).findByApiKey(anyString());
        verify(countryServiceMock, times(0)).findCountryCode(anyString());
        verify(weatherReportDaoServiceMock, times(0)).findByCityAndCountry(anyString(), anyString());
        verify(openWeatherServiceFactoryMock, times(0)).getWeatherReport(anyString(), anyString());
        verify(weatherReportDaoServiceMock, times(0)).saveOrUpdate(any(WeatherReportDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).save(any(ApiKeyDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).delete(anyString());
        verify(weatherReportDaoServiceMock, times(0)).deleteAllById(any(List.class));
    }

    @Test
    public void getWeatherReport_WILL_returnBadRequestError_WHEN_missingParameter_Q() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(apiKeyDetailServiceMock.findByApiKey(anyString())).thenReturn(Optional.empty());

        // Run test
        StringBuilder urlBuilder = new StringBuilder("/weather/report?");
        urlBuilder.append("apiKey=abbcdff498393983439de");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                                   .get(urlBuilder.toString())
                                                   .accept(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isBadRequest()).andReturn();

        // Verify
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        verify(apiKeyDetailServiceMock, times(0)).findByApiKey(anyString());
        verify(countryServiceMock, times(0)).findCountryCode(anyString());
        verify(weatherReportDaoServiceMock, times(0)).findByCityAndCountry(anyString(), anyString());
        verify(openWeatherServiceFactoryMock, times(0)).getWeatherReport(anyString(), anyString());
        verify(weatherReportDaoServiceMock, times(0)).saveOrUpdate(any(WeatherReportDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).save(any(ApiKeyDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).delete(anyString());
        verify(weatherReportDaoServiceMock, times(0)).deleteAllById(any(List.class));
    }

    @Test
    public void getWeatherReport_WILL_returnBadRequestError_WHEN_missingParameter_apiKey() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(apiKeyDetailServiceMock.findByApiKey(anyString())).thenReturn(Optional.empty());

        // Run test
        StringBuilder urlBuilder = new StringBuilder("/weather/report?");
        urlBuilder.append("q=").append(city).append(",").append(country);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                                   .get(urlBuilder.toString())
                                                   .accept(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isBadRequest()).andReturn();

        // Verify
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        verify(apiKeyDetailServiceMock, times(0)).findByApiKey(anyString());
        verify(countryServiceMock, times(0)).findCountryCode(anyString());
        verify(weatherReportDaoServiceMock, times(0)).findByCityAndCountry(anyString(), anyString());
        verify(openWeatherServiceFactoryMock, times(0)).getWeatherReport(anyString(), anyString());
        verify(weatherReportDaoServiceMock, times(0)).saveOrUpdate(any(WeatherReportDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).save(any(ApiKeyDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).delete(anyString());
        verify(weatherReportDaoServiceMock, times(0)).deleteAllById(any(List.class));
    }

    @Test
    public void getWeatherReport_WILL_returnBadRequestError_WHEN_noParametersExist() throws Exception {

        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(apiKeyDetailServiceMock.findByApiKey(anyString())).thenReturn(Optional.empty());

        // Run test
        StringBuilder urlBuilder = new StringBuilder("/weather/report?");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                                                   .get(urlBuilder.toString())
                                                   .accept(MediaType.APPLICATION_JSON))
                                  .andExpect(status().isBadRequest()).andReturn();

        // Verify
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());

        verify(apiKeyDetailServiceMock, times(0)).findByApiKey(anyString());
        verify(countryServiceMock, times(0)).findCountryCode(anyString());
        verify(weatherReportDaoServiceMock, times(0)).findByCityAndCountry(anyString(), anyString());
        verify(openWeatherServiceFactoryMock, times(0)).getWeatherReport(anyString(), anyString());
        verify(weatherReportDaoServiceMock, times(0)).saveOrUpdate(any(WeatherReportDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).save(any(ApiKeyDetail.class));
        verify(apiKeyDetailServiceMock, times(0)).delete(anyString());
        verify(weatherReportDaoServiceMock, times(0)).deleteAllById(any(List.class));
    }

    private WeatherReportDetail buildWeatherReportDetail(String city, String country) {
        WeatherReportDetail weatherReportDetail = new WeatherReportDetail();
        weatherReportDetail.setCity(capitaliseString(city));
        weatherReportDetail.setCountry(capitaliseString(country));
        weatherReportDetail.setTimestamp(new Date().getTime());
        weatherReportDetail.setDescription("This is a unit test");
        return weatherReportDetail;
    }
}
