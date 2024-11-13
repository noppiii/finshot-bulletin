package com.example.finshot.bulletin.security.oauth2;

import com.example.finshot.bulletin.constant.ErrorCode;
import com.example.finshot.bulletin.entity.SocialCode;
import com.example.finshot.bulletin.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
@Builder(access = AccessLevel.PRIVATE)
@SuppressWarnings("unchecked")
public class OAuth2Attribute {

    private Map<String, Object> attributes;
    private String attributeKey;
    private String email;
    private String name;
    private String picture;
    private String provider;
    private SocialCode socialCode;
    private String accessToken;

    public static OAuth2Attribute of(String provider, String accessToken, String attributeKey, Map<String, Object> attributes) {
        return switch (provider) {
            case "google" -> google(provider, accessToken, attributeKey, attributes);
            default -> throw new NotFoundException(ErrorCode.NOT_FOUND_PROVIDER);
        };
    }

    private static OAuth2Attribute google(String provider, String accessToken, String attributeKey, Map<String, Object> attributes) {
        log.debug("google: {}", attributes);
        return OAuth2Attribute.builder()
                .email((String) attributes.get("email"))
                .name((String) attributes.get("name"))
                .picture((String)attributes.get("picture"))
                .attributes(attributes)
                .attributeKey(attributeKey)
                .provider(provider)
                .socialCode(SocialCode.GOOGLE)
                .accessToken(accessToken)
                .build();
    }


    public Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", attributeKey);
        map.put("key", attributeKey);
        map.put("email", email);
        map.put("name", name);
        map.put("picture", picture);
        map.put("provider", provider);
        map.put("accessToken", accessToken);
        map.put("socialCode", socialCode);

        return map;
    }
}