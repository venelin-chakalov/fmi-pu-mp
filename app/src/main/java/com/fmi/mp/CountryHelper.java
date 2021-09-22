package com.fmi.mp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public final class CountryHelper {

    private static Map<String, CountryContainer> countries;

    private CountryHelper(){}

    public static void init() {
        countries = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();

        URL endpoint = null;
        try {
            String spec = "https://countriesnow.space/api/v0.1/countries/info?returns=currency,flag,unicodeFlag,dialCode";
            endpoint = new URL(spec);
            URL finalEndpoint = endpoint;
            Thread thread = new Thread(() -> {
                try {
                    HttpsURLConnection myConnection = (HttpsURLConnection) finalEndpoint.openConnection();
                    myConnection.setHostnameVerifier((hostname, session) -> true);
                    if (myConnection.getResponseCode() != 200) {
                        throw new RuntimeException("error getting the data");
                    }
                    InputStream responseBody = null;
                    responseBody = myConnection.getInputStream();
                    CountryResponse countryResponse = objectMapper.readValue(responseBody, CountryResponse.class);
                    for (CountryContainer c: countryResponse.getData()) {
                        countries.put(c.getDialCode(), c);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CountryContainer get(String code) {
        return countries.get(code);
    }

}

