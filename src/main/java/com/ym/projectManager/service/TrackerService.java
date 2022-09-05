package com.ym.projectManager.service;

import com.ym.projectManager.model.Parcel;
import com.ym.projectManager.repository.ParcelRepository;
import com.ym.projectManager.repository.TrackParcelRepository;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TrackerService {

    private String Apikey = "D377580BE09FDBCD101AF34889F3E093";

    private final ParcelRepository parcelRepository;
    private final TrackParcelRepository trackParcelRepository;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    public TrackerService(ParcelRepository parcelRepository, TrackParcelRepository trackParcelRepository) {
        this.parcelRepository = parcelRepository;
        this.trackParcelRepository = trackParcelRepository;
    }

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

        } else if (type.equals("batch")) {

            String ReqURL = "http://api.trackru.ru/v1/trackings/batch";
            bodyParams.add(requestData);
            result = sendPost(ReqURL, headerparams, bodyParams, "POST");

        } else if (type.equals("codeNumberGet")) {

            String ReqURL = "http://api.trackru.ru/v1/trackings";
            String RelUrl = ReqURL;
            result = sendGet(RelUrl, headerparams, "GET");

        } else if (type.equals("codeNumberPut")) {

            String ReqURL = "http://api.trackru.ru/v1/trackings";
            bodyParams.add(requestData);
            String RelUrl = ReqURL;
            result = sendPost(RelUrl, headerparams, bodyParams, "PUT");

        } else if (type.equals("codeNumberDelete")) {

            String ReqURL = "http://api.trackru.ru/v1/trackings";
            String RelUrl = ReqURL;
            result = sendGet(RelUrl, headerparams, "DELETE");

        } else if (type.equals("realtime")) {

            String ReqURL = "http://api.trackru.ru/v1/trackings/realtime";
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

    public static String sendGet(String url, Map<String, String> headerParams, String mothod) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);

            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();

            connection.setRequestMethod(mothod);

            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            connection.connect();

            Map<String, List<String>> map = connection.getHeaderFields();

            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }

            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("Exception " + e);
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }


}



