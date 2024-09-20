package weatherapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherReport {

    private String description;

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
