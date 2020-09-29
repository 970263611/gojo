package util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

/**
 * @Author dingwq
 * @Date 2020/9/24 16:23
 * @Description
 */
public class HttpClientUtil {

    private static RestTemplate restTemplate = new RestTemplate();

    public static Object httpClient(String url, Object obj, Class resultType) {
        return restTemplate.postForObject(url, obj, resultType);
    }

    public static Object httpClientExchange(String url, HttpMethod type, HttpEntity requestEntity, Class resultType) {
        return restTemplate.exchange(url, type, requestEntity, resultType);
    }
}
