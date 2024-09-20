package weatherapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import weatherapi.entity.ApiKeyDetail;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKeyDetail, String> {

}
