package in.kicka55studios.mysqltest;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class JSONParser {

    public String TAG_USERNAME = "username";
    public String TAG_PASSWORD = "password";

    // constructor
    public JSONParser() {

    }

    private String getQuery(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public String makeHttpGetRequest(String url_string) throws IOException {
        HttpURLConnection urlConnection = null;
        String jsonString = "";
        InputStream is = null;

        try {
            URL url = new URL(url_string);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(10000);
            urlConnection.connect();

            is = urlConnection.getInputStream();

            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            jsonString = buffer.toString();
            Log.i("MySQLtest", jsonString);

            reader.close();


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (is != null) {
                is.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return jsonString;
    }

    // function get json from url
    // by making HTTP POST or GET mehtod
    public String makeHttpRequest(String url_string, String method,
                                  HashMap<String, String> params) {
        URL url;
        String response = "";
        try {
            url = new URL(url_string);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();
            response = conn.getResponseMessage();

            String res = "";
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    res += line;
                }
                Log.e("MySQLtest", "response msg: " + res);
                return res;
            } else {
                Log.e("RequestError", "FAILED");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

}