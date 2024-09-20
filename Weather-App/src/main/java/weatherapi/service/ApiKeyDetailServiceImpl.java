package weatherapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weatherapi.entity.ApiKeyDetail;
import weatherapi.repository.ApiKeyRepository;

import java.util.Optional;

@Service
public class ApiKeyDetailServiceImpl implements ApiKeyDetailService {

    @Autowired
    private ApiKeyRepository apiKeyDao;

    @Override
    public ApiKeyDetail save(ApiKeyDetail apiKeyDetail) {
        return apiKeyDao.save(apiKeyDetail);
    }

    @Override
    public Optional<ApiKeyDetail> findByApiKey(String apiKey) {
        return apiKeyDao.findById(apiKey);
    }

    @Override
    public void delete(String apiKey) {
        apiKeyDao.deleteById(apiKey);
    }
}
