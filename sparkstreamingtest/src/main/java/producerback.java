/**
 * Created by kunalkrishna on 4/16/17.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class producerback {

    private static final String URL = "https://maps.googleapis.com/maps/api/geocode/json";

    public String getLatLong(String address) throws IOException {

        //System.out.println("Address: " + address);

        URL url = new URL(URL  + "&address="+ URLEncoder.encode(address, "UTF-8") + "&sensor=true");

        // Open the Connection
        URLConnection conn = url.openConnection();

        InputStream in = conn.getInputStream();
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null) {
            responseStrBuilder.append(inputStr + "\n");
        }

        System.out.println(responseStrBuilder.toString());
        JsonParser jsonParser = new JsonParser();
        JsonArray results = jsonParser.parse(responseStrBuilder.toString())
                .getAsJsonObject().getAsJsonArray("results");

        //System.out.println("Results: " + results.size());

        if(results == null || results.size() == 0) {
            return null;
        }
        JsonObject location = results.get(0)
                .getAsJsonObject().get("geometry")
                .getAsJsonObject().getAsJsonObject("location");

        String lat = location.get("lat").getAsString();
        String lng = location.get("lng").getAsString();


        return lat + "::" + lng;
    }

    /**
     * test driver program
     * @param args
     */
//    public static void main(String[] args) {
//        GeoLocationMapper geoLocationMapper = new GeoLocationMapper();
//
//        try {
//            System.out.println(geoLocationMapper.getLatLong("London, England"));
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

}

