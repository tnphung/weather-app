package weatherapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import weatherapi.entity.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {

}
