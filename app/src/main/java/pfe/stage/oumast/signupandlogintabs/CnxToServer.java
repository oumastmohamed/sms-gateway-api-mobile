package pfe.stage.oumast.signupandlogintabs;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CnxToServer {
    public static String mPost(HashMap<String, Object> objects, String u){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(u);

            String json = "";
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            try {
                for (Map.Entry<String, Object> entry : objects.entrySet()) {
                    jsonObject.put(""+entry.getKey(), entry.getValue()+"");
                }
            } catch (JSONException e) {
                Log.w("ERROR", e.getMessage());
                try {
                    jsonObject.put("error", e.getMessage());
                } catch (Exception a) {
                    Log.w("ERROR JSON", a.getMessage());
                }
            }

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            System.out.println("Exception : "+e.getLocalizedMessage());
        }

        // 11. return result
        System.out.println("-----------------------------> Result POST : "+result.toString());
        return result;
    }
    public static String POSTREST(HashMap<String, Object> objects, String u) {
        String json = null;
        String result = null;
        JSONObject jsonObject1 = new JSONObject();
        try {
            for (Map.Entry<String, Object> entry : objects.entrySet()) {
                jsonObject1.put(""+entry.getKey(), entry.getValue()+"");
            }
        } catch (JSONException e) {
            Log.w("ERROR", e.getMessage());
        }
        try {
            json = jsonObject1.toString();
            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("USER-AGENT", "Mozilla/5.0");
            conn.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //conn.setRequestMethod("POST");
            OutputStream os = conn.getOutputStream();
            os.write(json.getBytes("UTF-8"));//json.getBytes("UTF-8")
            os.close();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            in.close();
            conn.disconnect();
        } catch (Exception e) {
            Log.wtf("ERROR URI =>", e.getLocalizedMessage());
        }
        return result;
    }
    public static String GETSMS(String u){
        String result = null;
        try {
            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("USER-AGENT", "Mozilla/5.0");
            conn.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
            conn.setRequestProperty("Content-Type", "charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            in.close();
            conn.disconnect();
        } catch (Exception e) {
            Log.wtf("ERROR URI =>", e.getMessage());
            return null;
        }
        return result;
    }
    public static String GET(HashMap<String, Object> objects, String u) {
        String result = null;
        u += "?";
        try {
            int i = 0;
            for (Map.Entry<String, Object> entry : objects.entrySet()) {
                i++;
                if (i == objects.size()) {
                    u += entry.getKey() + "=" + entry.getValue();
                } else {
                    u += entry.getKey() + "=" + entry.getValue() + "&";
                }
            }
        } catch (Exception e) {
            Log.w("ERROR", e.getMessage());

            try {
                u += "error=" + e.getMessage();
            } catch (Exception a) {
                Log.w("ERROR JSON", a.getMessage());
                return null;
            }
        }
        try {
            URL url = new URL(u);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("USER-AGENT", "Mozilla/5.0");
            conn.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
            conn.setRequestProperty("Content-Type", "charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            in.close();
            conn.disconnect();
        } catch (Exception e) {
            Log.wtf("ERROR URI =>", e.getMessage());
            return null;
        }
        return result;
    }

    public static ArrayList<SmsObject> ArraysmsObject(String result){
        JSONObject jsonObject;
        JSONArray jsonArray;
        ArrayList<SmsObject> smsObjects= new ArrayList<>();
        try {

            jsonArray = new JSONArray(result);
        } catch(JSONException e) {
            System.out.println(" Convert catch error : ------> "+e.getMessage());
            return null;
        }
        // Convert json to smsObject
        try{
            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jo = jsonArray.getJSONObject(i);
                String id = jo.getString("id");
                String phone = jo.getString("phone");
                String message = jo.getString("message");
                SmsObject smsObject = new SmsObject(id, phone, message);
                smsObjects.add(smsObject);
            }
        }catch(Exception e){
            System.out.println(" Convert to smsObject catch error : ------> "+e.getMessage());
            return null;
        }
        return smsObjects;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;

    }

    public static String GETREST(String u) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpGet httpGet = new HttpGet(u);
        String text = "walo";
        try {
            HttpResponse response = httpClient.execute(httpGet, localContext);


            HttpEntity entity = response.getEntity();


            text = getASCIIContentFromEntity(entity);


        } catch (Exception e) {
            return e.getLocalizedMessage();
        }


        return text;
    }

    public static String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
        InputStream in = entity.getContent();


        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n>0) {
            byte[] b = new byte[4096];
            n =  in.read(b);


            if (n>0) out.append(new String(b, 0, n));
        }
        return out.toString();
    }
}