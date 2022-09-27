package com.ym.projectManager.service;

import com.ym.projectManager.conf.ConfigTrackApi;
import com.ym.projectManager.model.Parcel;
import com.ym.projectManager.repository.ParcelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TrackerService {

    private String Apikey = ConfigTrackApi.trackApiKey;

    private final ParcelRepository parcelRepository;

    public void registerParcelInTracker(String trackNum) {
        Parcel parcel = parcelRepository.findFirstByTrackNumber(trackNum);
        if (parcel == null) {
            String requestData = "[{\"number\": \"" + trackNum + "\"}]";

            try {
                String result = orderOnlineByJson(requestData, "register");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String orderOnlineByJson(String requestData, String type) throws Exception {
        //---headerParams
        Map<String, String> headerparams = new HashMap<String, String>();
        headerparams.put("17token", Apikey);
        headerparams.put("Content-Type", "application/json");
        //---bodyParams
        List<String> bodyParams = new ArrayList<String>();
        String result = null;
        if (type.equals("register")) {
            String ReqURL = "https://api.17track.net/track/v1/register";
            bodyParams.add(requestData);
            result = sendPost(ReqURL, headerparams, bodyParams, "POST");

        } else if (type.equals("getTrackInfo")) {

            String ReqURL = "https://api.17track.net/track/v1/gettrackinfo";
            bodyParams.add(requestData);
            result = sendPost(ReqURL, headerparams, bodyParams, "POST");

        }

        return result;

    }

    private String sendPost(String url, Map<String, String> headerParams, List<String> bodyParams, String mothod) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();

            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestMethod(mothod);

            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            conn.connect();

            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");

            StringBuffer sbBody = new StringBuffer();
            for (String str : bodyParams) {
                sbBody.append(str);
            }
            out.write(sbBody.toString());

            out.flush();

            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

}



