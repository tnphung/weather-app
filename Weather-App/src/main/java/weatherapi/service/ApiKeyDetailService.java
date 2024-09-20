package weatherapi.service;

import weatherapi.entity.ApiKeyDetail;

import java.util.Optional;

public interface ApiKeyDetailService {

    ApiKeyDetail save(ApiKeyDetail apiKeyDetail);
    Optional<ApiKeyDetail> findByApiKey(String apiKey);
    void delete(String apiKey);
}
