package weatherapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import weatherapi.entity.ApiKeyDetail;

public class ApiKey {

    private ApiKeyDetail apiKeyDetail;

    public ApiKey(ApiKeyDetail apiKeyDetail) {
        this.apiKeyDetail = apiKeyDetail;
    }

    @JsonProperty("api_key")
    public String getApiKey() {
        return apiKeyDetail.getApiKey();
    }
    @JsonProperty("timestamp")
    public Long getTimestamp() {
        return apiKeyDetail.getTimestamp();
    }
}
