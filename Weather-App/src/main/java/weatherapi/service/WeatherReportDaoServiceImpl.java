package weatherapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import weatherapi.entity.WeatherReportDetail;
import weatherapi.repository.WeatherReportRepository;

import java.util.List;

import static weatherapi.utility.Utils.capitaliseString;

@Service
public class WeatherReportDaoServiceImpl implements WeatherReportDaoService {

    @Autowired
    private WeatherReportRepository weatherReportDao;

    @Override
    public WeatherReportDetail saveOrUpdate(@NonNull WeatherReportDetail weatherReportDetail) {

        if (weatherReportDetail != null && StringUtils.hasText(weatherReportDetail.getCity()) && StringUtils.hasText(weatherReportDetail.getCountry())) {
            capitaliseString(weatherReportDetail.getCity());
            weatherReportDetail.setCity(capitaliseString(weatherReportDetail.getCity()));
            weatherReportDetail.setCountry(capitaliseString(weatherReportDetail.getCountry()));
        }
        return weatherReportDao.save(weatherReportDetail);
    }

    @Override
    public List<WeatherReportDetail> findByCityAndCountry(String city, String country) {
        return weatherReportDao.findByAllIgnoreCaseCityAndCountry(city, country);
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        weatherReportDao.deleteAllById(ids);
    }

    @Override
    public void delete(Long id) {
        weatherReportDao.deleteById(id);
    }

}
