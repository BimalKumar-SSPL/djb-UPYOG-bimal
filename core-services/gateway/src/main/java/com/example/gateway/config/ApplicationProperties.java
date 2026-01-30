package com.example.gateway.config;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class ApplicationProperties {
    @Value("${egov.auth-service-host}")
    private String authServiceHost;
    @Value("${egov.auth-service-uri}")
    private String authUri;
    @Value("${egov.auth-service-host}${egov.user.search.path}")
    private String userSearchURI;
    @Value("${spring.data.redis.default.replenishRate}")
    private Integer defaultReplenishRate;
    @Value("${spring.data.redis.default.burstCapacity}")
    private Integer defaultBurstCapacity;
    @Value("${egov.authorize.access.control.host}${egov.authorize.access.control.uri}")
    private String authorizationUrl;
    private List<String> encryptedUrlSet;
    private List<String> openEndpointsWhitelist;
    private List<String> mixedModeEndpointsWhitelist;
    @Value("${keycloak.issuer}")
    private String keycloakIssuer;
    @Value("${keycloak.jwks-url}")
    private String keycloakJwksUrl;
    @Value("${keycloak.audience}")
    private String keycloakAudience;

    @Value("${egov.encrypted-endpoints-list}")
    public void setEncryptedUrlListValues(List<String> encryptedListFromProperties) {
        this.encryptedUrlSet = Collections.unmodifiableList(encryptedListFromProperties);
    }

    @Value("${egov.open-endpoints-whitelist}")
    public void setOpenEndpointsWhitelistValues(List<String> openUrlListFromProperties) {
        this.openEndpointsWhitelist = Collections.unmodifiableList(openUrlListFromProperties);
    }

    @Value("${egov.mixed-mode-endpoints-whitelist}")
    public void setMixModeEndpointListValues(List<String> mixModeUrlListFromProperties) {
        this.mixedModeEndpointsWhitelist = Collections.unmodifiableList(mixModeUrlListFromProperties);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http.csrf(CsrfSpec::disable).authorizeExchange((ex) -> {
            ex.anyExchange().permitAll();
        }).build();
    }

    public String toString() {
        String var10000 = this.getAuthServiceHost();
        return "ApplicationProperties(authServiceHost=" + var10000 + ", authUri=" + this.getAuthUri() + ", userSearchURI=" + this.getUserSearchURI() + ", defaultReplenishRate=" + this.getDefaultReplenishRate() + ", defaultBurstCapacity=" + this.getDefaultBurstCapacity() + ", authorizationUrl=" + this.getAuthorizationUrl() + ", encryptedUrlSet=" + String.valueOf(this.getEncryptedUrlSet()) + ", openEndpointsWhitelist=" + String.valueOf(this.getOpenEndpointsWhitelist()) + ", mixedModeEndpointsWhitelist=" + String.valueOf(this.getMixedModeEndpointsWhitelist()) + ", keycloakIssuer=" + this.getKeycloakIssuer() + ", keycloakJwksUrl=" + this.getKeycloakJwksUrl() + ", keycloakAudience=" + this.getKeycloakAudience() + ")";
    }

    public void setAuthServiceHost(final String authServiceHost) {
        this.authServiceHost = authServiceHost;
    }

    public void setAuthUri(final String authUri) {
        this.authUri = authUri;
    }

    public void setUserSearchURI(final String userSearchURI) {
        this.userSearchURI = userSearchURI;
    }

    public void setDefaultReplenishRate(final Integer defaultReplenishRate) {
        this.defaultReplenishRate = defaultReplenishRate;
    }

    public void setDefaultBurstCapacity(final Integer defaultBurstCapacity) {
        this.defaultBurstCapacity = defaultBurstCapacity;
    }

    public void setAuthorizationUrl(final String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

    public void setEncryptedUrlSet(final List<String> encryptedUrlSet) {
        this.encryptedUrlSet = encryptedUrlSet;
    }

    public void setOpenEndpointsWhitelist(final List<String> openEndpointsWhitelist) {
        this.openEndpointsWhitelist = openEndpointsWhitelist;
    }

    public void setMixedModeEndpointsWhitelist(final List<String> mixedModeEndpointsWhitelist) {
        this.mixedModeEndpointsWhitelist = mixedModeEndpointsWhitelist;
    }

    public void setKeycloakIssuer(final String keycloakIssuer) {
        this.keycloakIssuer = keycloakIssuer;
    }

    public void setKeycloakJwksUrl(final String keycloakJwksUrl) {
        this.keycloakJwksUrl = keycloakJwksUrl;
    }

    public void setKeycloakAudience(final String keycloakAudience) {
        this.keycloakAudience = keycloakAudience;
    }

    public String getAuthServiceHost() {
        return this.authServiceHost;
    }

    public String getAuthUri() {
        return this.authUri;
    }

    public String getUserSearchURI() {
        return this.userSearchURI;
    }

    public Integer getDefaultReplenishRate() {
        return this.defaultReplenishRate;
    }

    public Integer getDefaultBurstCapacity() {
        return this.defaultBurstCapacity;
    }

    public String getAuthorizationUrl() {
        return this.authorizationUrl;
    }

    public List<String> getEncryptedUrlSet() {
        return this.encryptedUrlSet;
    }

    public List<String> getOpenEndpointsWhitelist() {
        return this.openEndpointsWhitelist;
    }

    public List<String> getMixedModeEndpointsWhitelist() {
        return this.mixedModeEndpointsWhitelist;
    }

    public String getKeycloakIssuer() {
        return this.keycloakIssuer;
    }

    public String getKeycloakJwksUrl() {
        return this.keycloakJwksUrl;
    }

    public String getKeycloakAudience() {
        return this.keycloakAudience;
    }
}
