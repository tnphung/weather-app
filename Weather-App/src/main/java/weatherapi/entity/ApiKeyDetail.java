package weatherapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "api_key")
public class ApiKeyDetail {

    private Long timestamp;
    private String apiKey;
    private int numberOfTimesUsed;

    @Id
    @Column(name = "api_key", nullable = false)
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Column(name = "timestamp", nullable = false)
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "number_of_time_used", nullable = false)
    public int getNumberOfTimesUsed() {
        return numberOfTimesUsed;
    }

    public void setNumberOfTimesUsed(int numberOfTimesUsed) {
        this.numberOfTimesUsed = numberOfTimesUsed;
    }
}
