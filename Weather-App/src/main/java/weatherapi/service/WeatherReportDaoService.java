package weatherapi.service;

import weatherapi.entity.WeatherReportDetail;

import java.util.List;

public interface WeatherReportDaoService {

    WeatherReportDetail saveOrUpdate(WeatherReportDetail weatherReportDetails);
    List<WeatherReportDetail> findByCityAndCountry(String city, String country);
    void deleteAllById(List<Long> id);
    void delete(Long id);
}
