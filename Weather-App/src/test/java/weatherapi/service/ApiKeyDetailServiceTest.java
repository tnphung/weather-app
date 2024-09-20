package weatherapi.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import weatherapi.BaseTest;
import weatherapi.entity.ApiKeyDetail;
import weatherapi.repository.ApiKeyRepository;
import weatherapi.utility.Utils;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApiKeyDetailServiceTest extends BaseTest {
    @Mock
    private ApiKeyRepository apiKeyDaoMock;
    @InjectMocks
    private ApiKeyDetailServiceImpl apiKeyDetailService;

    @Test
    public void save_WILL_saveApiKey_WHEN_noError() {

        // Give
        ApiKeyDetail apiKeyDetail = Utils.buildApiKeyDetail();
        when(apiKeyDaoMock.save(any(ApiKeyDetail.class))).thenReturn(apiKeyDetail);

        // Run test
        ApiKeyDetail actual = apiKeyDetailService.save(apiKeyDetail);

        // Verify actual
        assertNotNull(actual);
        assertNotNull(actual.getApiKey());
        verify(apiKeyDaoMock, times(1)).save(any(ApiKeyDetail.class));
    }

    @Test
    public void findByApiKey_WILL_returnApiKey_WHEN_apiKeyMatched() {

        // Give
        ApiKeyDetail expected = Utils.buildApiKeyDetail();
        when(apiKeyDaoMock.findById(anyString())).thenReturn(Optional.of(expected));

        // Run test
        Optional<ApiKeyDetail> actual = apiKeyDetailService.findByApiKey(expected.getApiKey());

        // Verify actual
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertEquals(expected.getApiKey(), actual.get().getApiKey());
        assertEquals(expected.getTimestamp(), actual.get().getTimestamp());
        verify(apiKeyDaoMock, times(1)).findById(anyString());
    }

    @Test
    public void findByApiKey_WILL_returnEmptyOptional_WHEN_apiKeyNotMatched() {

        // Give
        when(apiKeyDaoMock.findById(anyString())).thenReturn(Optional.empty());

        // Run test
        String wrongApiKey = "abcefbbad7789343edd";
        Optional<ApiKeyDetail> actual = apiKeyDetailService.findByApiKey(wrongApiKey);

        // Verify actual
        assertNotNull(actual);
        assertTrue(!actual.isPresent());
        verify(apiKeyDaoMock, times(1)).findById(anyString());
    }

    @Test
    public void delete_WILL_removeApiKeyDetails_WHEN_apiKeyMatched() {

        // Give
        doNothing().when(apiKeyDaoMock).deleteById(anyString());

        // Run test
        apiKeyDetailService.delete("abd93839");

        // Verify
        verify(apiKeyDaoMock).deleteById(anyString());
    }
}
