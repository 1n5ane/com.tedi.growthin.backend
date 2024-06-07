package com.tedi.growthin.backend.services.jwt

import com.tedi.growthin.backend.configuration.JwtUriConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.KeySpec
import java.security.spec.RSAPublicKeySpec

@Service
class PublicKeyService {
    @Autowired
    RestTemplate restTemplate

    @Autowired
    JwtUriConfiguration jwtUriConfiguration

    PublicKey getPublickKey(){

        try {
            def jwksResponse = restTemplate.getForObject(jwtUriConfiguration.jwkSetUri, Map.class)
            def res = ((jwksResponse.get("keys") as List).first() as Map)
            def publicKeyBase64 = res.get("n") as String
            def exponentBase64 = res.get("e") as String
            byte[] publicKeyBytes = Base64.getUrlDecoder().decode(publicKeyBase64)
            byte[] exponentBytes = Base64.getUrlDecoder().decode(exponentBase64)
            KeySpec keySpec = new RSAPublicKeySpec(new BigInteger(publicKeyBytes),new BigInteger(exponentBytes))
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(keySpec)
            return publicKey
        } catch (Exception e) {
            throw new RuntimeException("Failed to get public key from JWKS", e);
        }
    }
}
