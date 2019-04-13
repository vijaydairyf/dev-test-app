package com.devapp.devmain.json;

import android.net.Uri;
import android.util.Log;

import com.devapp.devmain.entity.PostEndShift;
import com.devapp.devmain.main.LoginActivity;
import com.devapp.devmain.main.SplashActivity;
import com.devapp.devmain.server.LogInService;
import com.devapp.devmain.server.ServerResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.SocketTimeoutException;
import java.util.List;


public class JSONParser {

    final static int CONNECTION_TIME_OUT = 10000;
    // static BufferedInputStream inputStream = null;
    public static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser() {

    }

    /**
     * Builds a new HttpClient with the same CookieStore than the previous one.
     * This allows to follow the http session, without keeping in memory the
     * full DefaultHttpClient.
     *
     * @author Régis Décamps <decamps@users.sf.net>
     */
    public static HttpClient getHttpClient() {

        final int CONN_WAIT_TIME = 10000;
        final int CONN_DATA_WAIT_TIME = 10000;

        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, CONN_WAIT_TIME);
        HttpConnectionParams.setSoTimeout(httpParams, CONN_DATA_WAIT_TIME);

        final DefaultHttpClient httpClient = new DefaultHttpClient();

        synchronized (LoginActivity.mLock) {
            if (LoginActivity.mCookie == null) {
                LoginActivity.mCookie = httpClient.getCookieStore();
            } else {
                httpClient.setCookieStore(LoginActivity.mCookie);
            }

        }
        return httpClient;
    }

    public static String getResponseBody(final HttpEntity entity)
            throws IOException, ParseException {

        if (entity == null) {
            throw new IllegalArgumentException("HTTP entity may not be null");
        }

        InputStream instream = entity.getContent();

        if (instream == null) {
            return "";
        }
        if (entity.getContentLength() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(

                    "HTTP entity too large to be buffered in memory");
        }
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                instream, HTTP.UTF_8));

        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            instream.close();
            reader.close();
        }

        return buffer.toString();

    }

    public static ServerResponse postData(String url, Object obj, int i) {
        // Create a new HttpClient and Post Header


        ServerResponse sr = new ServerResponse();

        try {
            String s = url.replaceAll(" ", "%20");
            Uri uri = Uri.parse(s);
            HttpPost httppost = new HttpPost(uri.toString());
            System.out.println("URL for post: " + uri.toString());

            StringEntity se = null;
            se = new StringEntity(getAllJsonBmcId(obj));
            // Set HTTP parameters
            httppost.setEntity(se);
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");
            // httppost.setHeader("Accept-Encoding", "gzip");
            // Execute HTTP Post Request
            HttpResponse response = getHttpClient().execute(httppost);

            if (response.getStatusLine().getStatusCode() == 200) {
                sr.setSuccess(true);
                sr.setResponseString(response.toString());
                SplashActivity.serVerPoEndShift = null;
            } else if (response.getStatusLine().getStatusCode() == 201) {
                sr.setSuccess(true);
                sr.setResponseString(response.toString());
                SplashActivity.serVerPoEndShift = null;

            } else {
                sr.setErrorMessage(response.toString());
                LogInService.isAuthenticated = false;
                if (i == 1) {
                    SplashActivity.serVerPoEndShift = (PostEndShift) obj;
                }

            }

            Log.v("HttpPostResponse",
                    ""
                            + String.valueOf(response.getStatusLine()
                            .getStatusCode()));
            System.out.println("JSON parser---Post response: "
                    + String.valueOf(response.getStatusLine().getStatusCode())
                    + "  Reason pharse: "
                    + response.getStatusLine().getReasonPhrase());

        } catch (ClientProtocolException e) {
            LogInService.isAuthenticated = false;
            if (i == 1) {
                SplashActivity.serVerPoEndShift = (PostEndShift) obj;
            }
            e.printStackTrace();
        } catch (IOException e) {
            LogInService.isAuthenticated = false;
            if (i == 1) {
                SplashActivity.serVerPoEndShift = (PostEndShift) obj;
            }
            e.printStackTrace();
        }
        return sr;
    }

    public static boolean getInvalidLogIn(JSONObject Jobject) {
        String StatusMsg = "statusMsg", statusCode = "statusCode";

        try {
            StatusMsg = Jobject.getString("statusMsg");
            statusCode = Jobject.getString("statusCode");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return statusCode.equalsIgnoreCase("408");

    }

    // TODO Auto-generated method stub
    public static JSONObject getHttpDelete(String gurl) {
        // TODO Auto-generated method stub
        String response = null;

        ServerResponse sr = new ServerResponse();

        // TODO Auto-generated method stub
        try {
            gurl = gurl.replaceAll(" ", "%20");
            Uri uri = Uri.parse(gurl);
            HttpDelete httpDelete = new HttpDelete(uri.toString());
            httpDelete.setHeader("Accept", "application/json");
            HttpResponse httpresponse = getHttpClient().execute(httpDelete);
            HttpEntity httpEntity = httpresponse.getEntity();
            response = getResponseBody(httpEntity);
            JSONObject jObject = null;
            try {
                jObject = new JSONObject(response);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            jObj = jObject;

            if (httpresponse.getStatusLine().getStatusCode() == 200) {
                sr.setSuccess(true);
                sr.setResponseString(response.toString());
                Log.v("ServerResponse", response.toString());
            } else {
                Log.v("ServerResponse", response.toString());
                sr.setErrorMessage(response.toString());
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jObj;

    }

    public static String getAllJsonBmcId(Object obj) {

        com.devapp.devmain.json.JacksonJSONConvert jacksonJSONConvert = new com.devapp.devmain.json.JacksonJSONConvert(obj);

        System.out.println("Post Json: " + com.devapp.devmain.json.JacksonJSONConvert.jsonObject);

        return com.devapp.devmain.json.JacksonJSONConvert.jsonObject;
    }

    public static ServerResponse putData(String url, Object obj) {
        // Create a new HttpClient and Post Header

        ServerResponse sr = new ServerResponse();

        try {
            String s = url.replaceAll(" ", "%20");
            Uri uri = Uri.parse(s);
            HttpPut httpPut = new HttpPut(uri.toString());

            System.out.println("URL for put rateChart update: " + uri.toString());
            StringEntity se = null;
            se = new StringEntity(getAllJsonBmcId(obj));
            // Set HTTP parameters
            httpPut.setEntity(se);
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-type", "application/json");
            // httppost.setHeader("Accept-Encoding", "gzip");
            // Execute HTTP Post Request
            HttpResponse response = getHttpClient().execute(httpPut);

            if (response.getStatusLine().getStatusCode() == 200) {
                sr.setSuccess(true);
                sr.setResponseString(response.toString());
            } else if (response.getStatusLine().getStatusCode() == 201) {
                sr.setSuccess(true);
                sr.setResponseString(response.toString());

            } else {
                sr.setErrorMessage(response.toString());
                LogInService.isAuthenticated = false;

            }

            System.out.println("JSON parser---Put response: "
                    + String.valueOf(response.getStatusLine().getStatusCode())
                    + "  Reason pharse: "
                    + response.getStatusLine().getReasonPhrase());

        } catch (ClientProtocolException e) {
            LogInService.isAuthenticated = false;

            e.printStackTrace();
        } catch (IOException e) {
            LogInService.isAuthenticated = false;

            e.printStackTrace();
        }
        return sr;
    }

    public static String getJsessionId(List<HttpCookie> cookies) {

        String jSessionId = null;
        if (!cookies.isEmpty()) {

            for (int i = 0; i < cookies.size(); i++) {
                HttpCookie cookie = cookies.get(i);
                if (cookie.getName().equalsIgnoreCase("JSESSIONID")) {
                    jSessionId = cookie.getValue();
                    break;
                }
            }
        }
        return jSessionId;

    }

    public String parseForLogIn(String url) {
        String response = "RESPONSE";
        jObj = null;
        // Making HTTP request
        try {
            // defaultHttpClient
            // DefaultHttpClient httpClient = new DefaultHttpClient();
            String s = url.replaceAll(" ", "%20");
            Uri uri = Uri.parse(s);

            HttpPost httpPost = new HttpPost(uri.toString());
            httpPost.addHeader("Accept", "application/json");
            HttpResponse httpResponse = getHttpClient().execute(httpPost);

            HttpEntity httpEntity = httpResponse.getEntity();
            response = getResponseBody(httpEntity);
        } catch (SocketTimeoutException e) {
            if (e != null) {
                System.out.println(e.getMessage());
            }
        } catch (ConnectTimeoutException e2) {
            System.out.println(e2.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response
                    .contains("<html><head><title>Login Page</title></head><body onload='document.f.j_username.focus()")) {
                jObj = null;
            }
        }

        return response;

    }

    public JSONObject getJSONFromUrl(String url) {
        String response = "RESPONSE";
        jObj = null;

        // Making HTTP request
        try {
            String s = url.replaceAll(" ", "%20");
            Uri uri = Uri.parse(s);

            HttpGet httpGet = new HttpGet(uri.toString());
            httpGet.addHeader("Accept", "application/json");

            HttpResponse httpResponse = getHttpClient().execute(httpGet);

            HttpEntity httpEntity = httpResponse.getEntity();
            response = getResponseBody(httpEntity);
            try {

                JSONObject jObject = new JSONObject(response);
                jObj = jObject;
                // Log.v("JsonParser", "JsonByteArry data: " +
                // jObj.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (SocketTimeoutException e) {
            if (e != null) {
                System.out.println(e.getMessage());
            }
        } catch (ConnectTimeoutException e2) {
            System.out.println(e2.getMessage());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response
                    .contains("<html><head><title>Login Page</title></head><body onload='document.f.j_username.focus()")) {
                jObj = null;
            }
        }

        return jObj;
    }

    public void getJsonwithByteArray(BufferedInputStream istream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int ctr;
        try {
            ctr = istream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = istream.read();
            }
            istream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("Text Data", byteArrayOutputStream.toString());
        try {

            // Parse the data into jsonobject to get original data in form of
            // json.
            JSONObject jObject = new JSONObject(
                    byteArrayOutputStream.toString());
            jObj = jObject;

            Log.v("JsonParser", "JsonByteArry data: " + jObj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
