package com.example.springsecurity;

import com.example.springsecurity.feign.CurrencyClient;
import com.example.springsecurity.feign.WeatherClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients(clients = {WeatherClient.class, CurrencyClient.class})
public class GatewayappApplication {
	public static void main(String[] args) {
		SpringApplication.run(GatewayappApplication.class, args);
	}
}
