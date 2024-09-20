package weatherapi.utility;

import org.springframework.util.StringUtils;
import weatherapi.entity.ApiKeyDetail;

import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;

public final class Utils {

    public static long ONE_HOUR = 3600000L; // In milliseconds
    private static int CALL_LIMIT = 5;
    private static Random random = new Random(99999);
    public static ApiKeyDetail buildApiKeyDetail() {
        ApiKeyDetail apiKeyDetail = new ApiKeyDetail();
        Date now = new Date();
        apiKeyDetail.setTimestamp(now.getTime());
        apiKeyDetail.setApiKey(findRandomChar(apiKeyDetail.getTimestamp()));
        return apiKeyDetail;
    }

    /**
     * Returns true if they API key has not been used 5 times within an hour. Otherwise, false is returned.
     * @param apiKeyDetail
     * @return true if they API key has not been used to 5 times within an hour. Otherwise, false is returned.
     */
    public static boolean validateApiKey(ApiKeyDetail apiKeyDetail) {
        if (apiKeyDetail != null) {
            Date now = new Date();
            Long diff = now.getTime() - apiKeyDetail.getTimestamp();
            return diff < ONE_HOUR && apiKeyDetail.getNumberOfTimesUsed() < CALL_LIMIT;
        }
        return false;
    }

    /**
     * Reset API key when it is more than one-hour old and has not reached its limit.
     * @param apiKeyDetail
     */
    public static ApiKeyDetail resetTimestampAndCallCount(ApiKeyDetail apiKeyDetail) {

        Date now = new Date();
        long diff = now.getTime() - apiKeyDetail.getTimestamp().longValue();

        if (diff > ONE_HOUR && apiKeyDetail.getNumberOfTimesUsed() < CALL_LIMIT) {
            ApiKeyDetail newApiKeyDetail = new ApiKeyDetail();
            newApiKeyDetail.setApiKey(apiKeyDetail.getApiKey());
            newApiKeyDetail.setTimestamp(now.getTime());
            newApiKeyDetail.setNumberOfTimesUsed(0);
            return newApiKeyDetail;
        }
        return apiKeyDetail;
    }

    /**
     * Validates the timestamp of a weather report.
     * @param timestamp
     * @return True if the weather report is less than one-hour old. Otherwise, false is returned.
     */
    public static boolean hasWeatherReportTimestampExpired(Long timestamp) {

        Date now = new Date();
        Long diff = now.getTime() - timestamp;
        return diff > ONE_HOUR;
    }

    /**
     * Capitalise an input string.
     * @param inputString
     * @return
     */
    public static String capitaliseString(String inputString) {

        StringBuilder stringBuilder = new StringBuilder();
        StringTokenizer stringTokenizer = new StringTokenizer(inputString, " ");

        while(stringTokenizer.hasMoreTokens()) {
            if(stringBuilder.length() > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(StringUtils.capitalize(stringTokenizer.nextToken()));
        }
        return stringBuilder.toString();
    }

    private static String findRandomChar(long timestamp) {
        long randomValue = random.nextLong();
        return Long.toHexString(randomValue) + Long.toHexString(timestamp);
    }

}
