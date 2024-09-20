package weatherapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import weatherapi.entity.WeatherReportDetail;

import java.util.List;

@Repository
public interface WeatherReportRepository extends JpaRepository<WeatherReportDetail, Long> {

    List<WeatherReportDetail> findByAllIgnoreCaseCityAndCountry(String city, String country);
}
