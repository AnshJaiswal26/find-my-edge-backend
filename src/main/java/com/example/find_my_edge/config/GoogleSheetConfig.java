package com.example.find_my_edge.config;

import com.example.find_my_edge.integrations.sheets.exception.SheetFetchException;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.List;

@Configuration
public class GoogleSheetConfig {

    @Bean
    public Sheets sheets() {
        try {
            InputStream inputStream = new ClassPathResource("credentials/service-account.json").getInputStream();

            GoogleCredentials credentials = ServiceAccountCredentials.fromStream(inputStream)
                                                                     .createScoped(
                                                                             List.of("https://www.googleapis.com/auth/spreadsheets"));

            return new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            ).setApplicationName("FindMyEdge")
             .build();

        } catch (Exception e) {
            throw new SheetFetchException("Something went wrong while connecting google sheets" + e.getMessage());
        }
    }
}
