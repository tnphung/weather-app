package weatherapi.utility;

import org.junit.jupiter.api.Test;
import weatherapi.BaseTest;
import weatherapi.entity.ApiKeyDetail;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static weatherapi.utility.Utils.ONE_HOUR;

public class UtilsTest extends BaseTest {

    @Test
    public void buildApiKey_WILL_returnApiKey_WHEN_successful() {

        // Run test
        ApiKeyDetail apiKeyDetail = Utils.buildApiKeyDetail();

        // Verify result
        assertNotNull(apiKeyDetail);
        assertNotNull(apiKeyDetail.getApiKey());
        assertNotNull(apiKeyDetail.getTimestamp());
        assertEquals(0, apiKeyDetail.getNumberOfTimesUsed());

    }

    @Test
    public void validateApiKey_WILL_returnTrue_WHEN_keyValid() {

        // Given
        ApiKeyDetail apiKeyDetail = Utils.buildApiKeyDetail();

        // Run test
        boolean result = Utils.validateApiKey(apiKeyDetail);

        // Verify result
        assertTrue(result);
    }

    @Test
    public void validateApiKey_WILL_returnFalse_WHEN_exceedsLimit() {

        // Given
        ApiKeyDetail apiKeyDetail = Utils.buildApiKeyDetail();
        apiKeyDetail.setNumberOfTimesUsed(5);

        // Run test
        boolean result = Utils.validateApiKey(apiKeyDetail);

        // Verify result
        assertFalse(result);
    }

    @Test
    public void validateApiKey_WILL_returnFalse_WHEN_apiKeyExpired() {

        // Given
        ApiKeyDetail apiKeyDetail = Utils.buildApiKeyDetail();
        Date now = new Date();
        apiKeyDetail.setTimestamp(now.getTime() - ONE_HOUR);

        // Run test
        boolean result = Utils.validateApiKey(apiKeyDetail);

        // Verify result
        assertFalse(result);
    }

    @Test
    public void validateApiKey_WILL_returnTrue_WHEN_halfAnHourHasElapsed() {

        // Given
        ApiKeyDetail apiKeyDetail = Utils.buildApiKeyDetail();
        Date now = new Date();
        apiKeyDetail.setTimestamp(now.getTime() - ONE_HOUR / 2);

        // Run test
        boolean result = Utils.validateApiKey(apiKeyDetail);

        // Verify result
        assertTrue(result);
    }

    @Test
    public void resetTimestampAndCallCount_WILL_updateApiKeyDetails_WHEN_elapsedTimeOverOneHour() {

        // Given
        ApiKeyDetail old = Utils.buildApiKeyDetail();
        Date now = new Date();
        old.setTimestamp(now.getTime() - ONE_HOUR - 1);
        old.setNumberOfTimesUsed(4);

        // Run test
        ApiKeyDetail updated = Utils.resetTimestampAndCallCount(old);

        // Verify result
        assertNotNull(updated);
        assertEquals(now.getTime(), updated.getTimestamp().longValue());
        assertTrue(updated.getTimestamp() > old.getTimestamp());
        assertEquals(0, updated.getNumberOfTimesUsed());
        assertTrue(old.getApiKey().equals(updated.getApiKey()));
    }

    @Test
    public void resetTimestampAndCallCount_WILL_notUpdateApiKeyDetails_WHEN_elapsedTimeUnderOneHour() {

        // Given
        ApiKeyDetail old = Utils.buildApiKeyDetail();
        Date now = new Date();
        old.setTimestamp(now.getTime() - ONE_HOUR + 1);
        old.setNumberOfTimesUsed(4);

        // Run test
        ApiKeyDetail updated = Utils.resetTimestampAndCallCount(old);

        // Verify result
        assertNotNull(updated);
        assertTrue(old.getTimestamp().equals(updated.getTimestamp()));
        assertEquals(old.getNumberOfTimesUsed(), updated.getNumberOfTimesUsed());
        assertTrue(old.getApiKey().equals(updated.getApiKey()));
    }
}
