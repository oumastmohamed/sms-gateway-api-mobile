package pfe.stage.oumast.signupandlogintabs;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Smsgateway {

    public Smsgateway() {
    }

    public String POSTREST(HashMap<String, Object> objects, String u) {
        String json = null;
        String result = "NOT VALUE";
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;
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

            try {
                HttpPost post = new HttpPost(u);
                StringEntity se = new StringEntity(json.toString());
                se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                post.setEntity(se);
                response = client.execute(post);
                HomeActivity.refreshT("response : "+response);
                /*Checking response */
                if(response!=null){
                    InputStream in = response.getEntity().getContent(); //Get the data in the entity
                    result = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
                    //JSONObject jsonObject = new JSONObject(result);
                    in.close();
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.wtf("ERROR URI =>", e.getLocalizedMessage());
        }
        return result;
    }
}