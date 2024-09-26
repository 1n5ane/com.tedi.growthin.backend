package com.tedi.growthin.backend.configuration

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration

import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.X509Certificate
import java.util.stream.Collectors

@Configuration
class KeyStoreConfiguration {
    KeyStore keyStore

    @Autowired
    SslConfiguration sslConfiguration

    @PostConstruct
    def initializeKeyStore(){
        FileInputStream is = new FileInputStream(sslConfiguration.keyStore)
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(is, sslConfiguration.keyStorePassword.toCharArray())
        this.keyStore = keyStore
    }

    List<Certificate> getTrustedCertificates(){
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(this.keyStore)

        List<TrustManager> trustManagers = Arrays.asList(trustManagerFactory.getTrustManagers())
        List<X509Certificate> certificates = trustManagers.stream()
                .filter(X509TrustManager.class::isInstance)
                .map(X509TrustManager.class::cast)
                .map(trustManager -> Arrays.asList(trustManager.getAcceptedIssuers()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList())
        return certificates
    }
}
