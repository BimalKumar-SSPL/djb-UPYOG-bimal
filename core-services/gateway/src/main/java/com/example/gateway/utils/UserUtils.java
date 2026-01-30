package com.example.gateway.utils;

import com.example.gateway.config.ApplicationProperties;
import java.util.Collections;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.User;
import org.egov.common.contract.user.UserDetailResponse;
import org.egov.common.contract.user.UserSearchRequest;
import org.egov.common.utils.MultiStateInstanceUtil;
import org.egov.tracer.model.CustomException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import static com.example.gateway.constants.GatewayConstants.CORRELATION_ID_HEADER_NAME;
import static com.example.gateway.constants.GatewayConstants.REQUEST_TENANT_ID_KEY;

@Slf4j
@Component
public class UserUtils {

    private RestTemplate restTemplate;
    private ApplicationProperties applicationProperties;
    private MultiStateInstanceUtil multiStateInstanceUtil;
    private final KeycloakTokenValidator tokenValidator;

    public UserUtils(RestTemplate restTemplate, ApplicationProperties applicationProperties, KeycloakTokenValidator tokenValidator) {
        this.restTemplate = restTemplate;
        this.applicationProperties = applicationProperties;
        this.tokenValidator = tokenValidator;
    }

    public User getUser(String authToken) {
        Jwt jwt;
        try {
            log.info("Checking token: {}", authToken);
            jwt = this.tokenValidator.validate(authToken);
        } catch (JwtException var10) {
            throw new CustomException("INVALID_TOKEN", var10.getMessage());
        }

        String preferredUsername = jwt.getClaimAsString("preferred_username");
        log.info("preferred_username: {}", preferredUsername);
        String email = jwt.getClaimAsString("email");
        log.info("email: {}", email);
        String sub = jwt.getSubject();
        log.info("sub: {}", sub);
        String authURL = String.format("%s%s%s", this.applicationProperties.getAuthServiceHost(), this.applicationProperties.getAuthUri(), authToken);

        try {
            User user = (User)this.restTemplate.postForObject(authURL, (Object)null, User.class, new Object[0]);
            log.info(user.toString());
            return user;
        } catch (Exception var9) {
            throw new CustomException("Exception occurred while fetching user: ", var9.getMessage());
        }
    }

    @Cacheable(value = "systemUser" , sync = true)
    public User fetchSystemUser(String tenantId, String correlationId) {

        UserSearchRequest userSearchRequest =new UserSearchRequest();
        userSearchRequest.setRoleCodes(Collections.singletonList("ANONYMOUS"));
        userSearchRequest.setUserType("SYSTEM");
        userSearchRequest.setPageSize(1);
        userSearchRequest.setTenantId(tenantId);

        final HttpHeaders headers = new HttpHeaders();
        headers.add(CORRELATION_ID_HEADER_NAME, correlationId);
        if (multiStateInstanceUtil.getIsEnvironmentCentralInstance())
            headers.add(REQUEST_TENANT_ID_KEY, tenantId);
        final HttpEntity<Object> httpEntity = new HttpEntity<>(userSearchRequest, headers);

        StringBuilder uri = new StringBuilder(applicationProperties.getUserSearchURI());
        User user = null;
        try {
            UserDetailResponse response = restTemplate.postForObject(uri.toString(), httpEntity, UserDetailResponse.class);
            if (!CollectionUtils.isEmpty(response.getUser()))
                user = response.getUser().get(0);
        } catch(Exception e) {
            log.error("Exception while fetching system user: ",e);
        }

        /*if(user == null)
            throw new CustomException("NO_SYSTEUSER_FOUND","No system user found");*/

        return user;
    }

}
