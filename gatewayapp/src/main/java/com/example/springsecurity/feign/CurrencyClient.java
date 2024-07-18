package com.example.springsecurity.feign;

import com.example.springsecurity.feign.dto.CurrencyResponse;
import feign.Client;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@FeignClient(name = "currencyClient", url = "https://api.exchangerate-api.com/v4/latest", configuration = CurrencyClient.CurrencyClientConfiguration.class)
public interface CurrencyClient {

    @GetMapping("/{base}")
    CurrencyResponse getCurrencies(@PathVariable("base") String base);

    @Configuration
    class CurrencyClientConfiguration {

        @Bean
        public Client feignClient() throws NoSuchAlgorithmException, KeyManagementException {
            return new Client.Default(getTrustingClient(), null);
        }

        private SSLSocketFactory getTrustingClient() throws NoSuchAlgorithmException, KeyManagementException {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        }

        private static final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };
    }
}