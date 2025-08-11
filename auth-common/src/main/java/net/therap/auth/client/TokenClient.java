package net.therap.auth.client;

import net.therap.auth.dto.RefreshTokenRequestDto;
import net.therap.auth.dto.TokenResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author apurboturjo
 * @since 8/3/25
 */
@Component
public class TokenClient {

    private static final String SERVICE_NAME = "auth";
    private static final String REFRESH_PATH = "/auth/api/refresh";

    @Autowired
    private ServiceDiscoveryCacheForAuthCommon serviceDiscoveryCacheForAuthCommon;

    private final RestTemplate restTemplate = new RestTemplate();

    private String getServiceBaseUrl() {
        Map<String, Object> instance = serviceDiscoveryCacheForAuthCommon.getInstance(SERVICE_NAME);
        String host = (String) instance.get("host");
        Integer port = (Integer) instance.get("port");
        return "http:" + host + ":" + port;
    }

    public TokenResponseDto refreshToken(RefreshTokenRequestDto requestDto) {
        String url = getServiceBaseUrl() + REFRESH_PATH;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RefreshTokenRequestDto> entity = new HttpEntity<>(requestDto, headers);
        ResponseEntity<TokenResponseDto> response =
                restTemplate.postForEntity(url, entity, TokenResponseDto.class);

        return response.getBody();
    }
}