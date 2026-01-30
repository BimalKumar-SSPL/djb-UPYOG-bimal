package com.example.gateway.utils;

import com.example.gateway.config.ApplicationProperties;
import java.util.List;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class KeycloakTokenValidator {
   private final JwtDecoder decoder;

   public KeycloakTokenValidator(ApplicationProperties props) {
      NimbusJwtDecoder nimbus = NimbusJwtDecoder.withJwkSetUri(props.getKeycloakJwksUrl()).build();
      OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(props.getKeycloakIssuer());
      OAuth2TokenValidator<Jwt> withAudience = new AudienceValidator(props.getKeycloakAudience());
      nimbus.setJwtValidator(new DelegatingOAuth2TokenValidator(new OAuth2TokenValidator[]{withIssuer, withAudience}));
      this.decoder = nimbus;
   }

   public Jwt validate(String token) throws JwtException {
      return this.decoder.decode(token);
   }

   static class AudienceValidator implements OAuth2TokenValidator<Jwt> {
      private final String audience;

      AudienceValidator(String audience) {
         this.audience = audience;
      }

      public OAuth2TokenValidatorResult validate(Jwt jwt) {
         List<String> aud = jwt.getAudience();
         return aud != null && aud.contains(this.audience) ? OAuth2TokenValidatorResult.success() : OAuth2TokenValidatorResult.failure(new OAuth2Error[]{new OAuth2Error("invalid_token", "Missing required audience: " + this.audience, (String)null)});
      }
   }
}
