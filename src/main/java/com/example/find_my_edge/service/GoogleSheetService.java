package com.example.find_my_edge.service;

import com.example.find_my_edge.dto.GoogleSheetRequest;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleSheetService {

    private final Sheets sheets;


    public GoogleSheetService() throws IOException, GeneralSecurityException {
        InputStream inputStream = new ClassPathResource("credentials/service-account.json").getInputStream();

        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(inputStream)
                                                                 .createScoped(List.of(
                                                                         "https://www.googleapis.com/auth/spreadsheets"));

        this.sheets = new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
        ).setApplicationName("FindMyEdge").build();

    }

    public ResponseEntity<String> appendDataToSheet(GoogleSheetRequest request) {
        try {

            String spreadsheetId = request.getSheetId();
            String range = request.getSheetName() + "!A1";

            ValueRange body = new ValueRange().setValues(List.of(new ArrayList<>(request.getData())));

            sheets.spreadsheets()
                  .values()
                  .append(spreadsheetId, range, body)
                  .setValueInputOption("USER_ENTERED")
                  .execute();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return new ResponseEntity<>("Row inserted!", HttpStatus.OK);
    }


    public ResponseEntity<List<String>> getSheetNames(String sheetId) {

        List<String> sheetNames = null;
        try {
            Spreadsheet spreadsheet = sheets.spreadsheets().get(sheetId).execute();
            sheetNames = spreadsheet.getSheets().stream().map(sheet -> sheet.getProperties().getTitle()).toList();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        if (sheetNames == null || sheetNames.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(sheetNames, HttpStatus.OK);
    }
}
