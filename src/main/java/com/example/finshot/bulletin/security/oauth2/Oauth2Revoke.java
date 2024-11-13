package com.example.finshot.bulletin.security.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Slf4j
public class Oauth2Revoke {

    @Value("${spring.security.oauth2.google-revoke-uri}")
    private String googleRevokeUri;

    private final RestTemplate restTemplate = new RestTemplate();

    public void revokeGoogle(String socialAccessToken) {
        log.debug("google revoke..");

        UriComponents uriBuilder = UriComponentsBuilder.fromUriString(googleRevokeUri)
                .queryParam("token", socialAccessToken)
                .build();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);

        restTemplate.exchange(
                uriBuilder.toString(),
                HttpMethod.POST,
                httpEntity,
                Void.class
        );
        log.debug("revoke google success!");
    }

}

