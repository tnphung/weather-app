package weatherapi.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import weatherapi.BaseTest;
import weatherapi.entity.WeatherReportDetail;
import weatherapi.repository.WeatherReportRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static weatherapi.utility.Utils.capitaliseString;

public class WeatherReportDaoServiceTest extends BaseTest {

    @Mock
    private WeatherReportRepository weatherReportDaoMock;

    @InjectMocks
    private WeatherReportDaoServiceImpl weatherReportDaoService;

    @Test
    public void saveOrUpdate_WILL_saveWeatherReportDetail_WHEN_noError() {

        // Give
        String city = "new york";
        String country = "united states";
        WeatherReportDetail weatherReportDetail = buildWeatherReportDetail(city, country);
        when(weatherReportDaoMock.save(any(WeatherReportDetail.class))).thenReturn(weatherReportDetail);

        // Run test
        WeatherReportDetail actual = weatherReportDaoService.saveOrUpdate(weatherReportDetail);

        // Verify result
        assertNotNull(actual);
        verify(weatherReportDaoMock, times(1)).save(any(WeatherReportDetail.class));
    }

    @Test
    public void findByCityAndCountry_WILL_returnWeatherReportDetails_WHEN_cityAndCountryNameMatched() {

        // Give

        List<WeatherReportDetail> weatherReportDetailList = buildWeatherReportDetailList(1);
        when(weatherReportDaoMock.findByAllIgnoreCaseCityAndCountry(anyString(), anyString())).thenReturn(weatherReportDetailList);

        // Run test
        List<WeatherReportDetail> actual = weatherReportDaoService.findByCityAndCountry("city-1", "country-1");

        // Verify result
        assertNotNull(actual);
        assertEquals(1, actual.size());
        verify(weatherReportDaoMock, times(1)).findByAllIgnoreCaseCityAndCountry(anyString(), anyString());

    }

    @Test
    public void deleteAllById_WILL_deleteAll_WHEN_noError() {

        // Give
        doNothing().when(weatherReportDaoMock).deleteAllById(any(List.class));
        List<Long> ids = new ArrayList<>();
        ids.add(122L);
        ids.add(155L);
        ids.add(12234L);

        // Run test
        weatherReportDaoService.deleteAllById(ids);

        // Verify result
        verify(weatherReportDaoMock, times(1)).deleteAllById(any(List.class));

    }

    @Test
    public void delete_WILL_deleteWeatherReportDetail_WHEN_noError() {

        // Give
        doNothing().when(weatherReportDaoMock).deleteById(any(Long.class));

        // Run test
        weatherReportDaoService.delete(1200L);

        // Verify result
        verify(weatherReportDaoMock, times(1)).deleteById(any(Long.class));
    }

    private List<WeatherReportDetail> buildWeatherReportDetailList(int count) {

        WeatherReportDetail weatherReportDetail;
        List<WeatherReportDetail> theList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            theList.add(buildWeatherReportDetail("city-" + i, "country-" + i));
        }
        return theList;
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
