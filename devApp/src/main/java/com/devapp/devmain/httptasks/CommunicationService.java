package com.devapp.devmain.httptasks;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by xxx on 9/4/15.
 */
public class CommunicationService {

    private static final String TAG = "COMMUNICATION-SERVICE";

    private static final String REDIRECT_TO_FORM_LOGIN_URI = "/amcu/spring_security_login";
    private static final String LOGIN_SUCCESS_URI = "/amcu/login_success";
    private static final String LOGIN_FAILURE_URI = "/amcu/all/login_failure";

    // by default 302s are not followed automatically.
    // Use the flag FOLLOW_REDIRECTS o follow redirects
    private static final boolean FOLLOW_REDIRECTS = true;
    private static final boolean DO_NOT_FOLLOW_REDIRECTS = false;
    private static final int CONNECTION_TIMEOUT = 3 * 60 * 1000;
    private static final int READ_TIMEOUT = 3 * 60 * 1000;

    private AuthenticationParameters authParams = null;
    private String serverUrl = "";
    private SSLContext sslContext = null;


    /**
     * @param serverUrl
     * @param authParams
     * @throws IOException
     * @throws SSLContextCreationException
     */
    public CommunicationService(String serverUrl, AuthenticationParameters authParams) throws IOException, SSLContextCreationException {

        this.serverUrl = serverUrl;
        this.authParams = authParams;

        try {
            System.setProperty("http.keepAlive", "true");
        } catch (Exception e) {
            e.printStackTrace();
        }


        //Use  this only for self signed certificate
        if (authParams.getCertificate() != null) {
            sslContext = SSLContextFactory.getInstance().createSSLContext(authParams.getCertificate());
        }

        if (CookieHandler.getDefault() == null) {
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        }
    }


    /**
     * @return
     * @throws IOException
     * @throws IncompatibleProtocolException
     */
    public HttpResponse login() throws IOException, IncompatibleProtocolException {

        String url = buildLoginUrl();
        HttpResponse response = sendMessage(url, "POST", null, false);

        System.out.print("LogIn url: " + url);
        if (response.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
            String location = getHeaderValue("Location", response.getHeaderFields());
            // Retrieving Success URIs will result in DPN Notifications!!!!
            // Can register subscriber
            if (location != null && location.contains(LOGIN_SUCCESS_URI) || location.contains(LOGIN_FAILURE_URI)) {
                response = redirect(response);
                //Log.d(TAG, "Login Response : " + response.getResponseCode() + " " + response.getResponseBody());
            }
        }
        return response;
    }

    /**
     * @return
     * @throws IOException
     * @throws IncompatibleProtocolException
     */
    public HttpResponse logout() throws IOException, IncompatibleProtocolException {

        String url = buildLogoutUrl();
        HttpResponse response = sendMessage(url, "POST", null, false);
        if (response.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
            response = redirect(response);
            //Log.d(TAG, "Login Response : " + response.getResponseCode() + " " + response.getResponseBody());
        }
        return response;
    }

    /**
     * @param uri
     * @return
     * @throws IOException
     * @throws IncompatibleProtocolException
     */
    public HttpResponse doGet(String uri) throws IOException, IncompatibleProtocolException {

        if (this.serverUrl == null || uri == null)
            throw new IncompatibleProtocolException("SERVER URL IS NULL");

        String url = (this.serverUrl.endsWith("/") ?
                this.serverUrl.substring(0, this.serverUrl.length() - 1) + uri : this.serverUrl + uri);

        //Log.d(TAG, "doGet:GET url " + url);
        HttpResponse gResponse = getMessage(url);

        int gResponseCode = gResponse.getResponseCode();
        //Log.d(TAG, "doGet:GET Response Code " + gResponseCode);
        //Log.d(TAG, "doGet:GET Response Code " + gResponse.getResponseBody());
        // If the response code is 302 with a Location header for Login
        // Try to login,if login succeeds attempt to Get again
        if (gResponseCode == HttpURLConnection.HTTP_MOVED_TEMP) {

            String location = getHeaderValue("Location", gResponse.getHeaderFields());
            //Log.d(TAG, "doGet:GET Redirect URI:" + location);
            if (location.contains(REDIRECT_TO_FORM_LOGIN_URI)) {
                HttpResponse lResponse = login();
                if (reattemptRequestToUriAfterLogin(uri, lResponse)) {
                    gResponse = getMessage(url);
                    //Log.d(TAG, "doGet:GET Reattempt-Response Code:" + gResponseCode);
                    //Log.d(TAG, "doGet:GET Reattempt-Response Body:" + gResponse.getResponseBody());
                }
            }
        }

        return gResponse;
    }

    /**
     * @param uri
     * @param data
     * @return
     * @throws IOException
     * @throws IncompatibleProtocolException
     */
    public HttpResponse doPost(String uri, String data) throws IOException, IncompatibleProtocolException {
        HttpResponse response = this.doPost(uri, data, false);
        return response;
    }

    /**
     * @param uri
     * @param data
     * @param compress
     * @return
     * @throws IOException
     * @throws IncompatibleProtocolException
     */
    public HttpResponse doPost(String uri, String data, boolean compress)
            throws IOException, IncompatibleProtocolException {

        if (this.serverUrl == null || uri == null)
            throw new IncompatibleProtocolException("INVALID URL");

        String url = (this.serverUrl.endsWith("/") ?
                this.serverUrl.substring(0, this.serverUrl.length() - 1) + uri : this.serverUrl + uri);

        //Log.d(TAG, "doPost:POST url " + url);
        //Log.d(TAG, "doPost:POST data " + (data == null ? "" : data));

        System.out.println("post url: " + url);

        HttpResponse pResponse = sendMessage(url, "POST", data, compress);

        int pResponseCode = pResponse.getResponseCode();
        //Log.d(TAG, "doPost:POST Response Code:" + pResponseCode);

        // If the response code is 302 with a Location header for Login
        // Try to login,if login succeeds attempt to post agin
        if (pResponseCode == HttpURLConnection.HTTP_MOVED_TEMP) {

            //Log.d(TAG, "doPost: POST Attempt response:" + pResponseCode);
            String location = getHeaderValue("Location", pResponse.getHeaderFields());
            //Log.d(TAG, "doPost: POST Redirect URI:" + location);
            if (location.contains(REDIRECT_TO_FORM_LOGIN_URI)) {
                HttpResponse loginResponse = login();
                if (reattemptRequestToUriAfterLogin(uri, loginResponse)) {
                    pResponse = sendMessage(url, "POST", data, compress);
                    //Log.d(TAG, "doPost: POST Reattempt response:" + pResponse.getResponseCode());
                }
            }
        }
        return pResponse;
    }


    /**
     * @param uri
     * @param data
     * @return
     * @throws IOException
     * @throws IncompatibleProtocolException
     */
    public HttpResponse doPut(String uri, String data) throws IOException, IncompatibleProtocolException {

        if (this.serverUrl == null || uri == null)
            throw new IncompatibleProtocolException("INVALID URL");

        String url = (this.serverUrl.endsWith("/") ?
                this.serverUrl.substring(0, this.serverUrl.length() - 1) + uri : this.serverUrl + uri);

        //Log.d(TAG, "doPost:POST url " + url);
        //Log.d(TAG, "doPost:POST data " + (data == null ? "" : data));

        HttpResponse pResponse = sendMessage(url, "PUT", data, false);

        int pResponseCode = pResponse.getResponseCode();
        //  Log.d(TAG, "doPost:POST Response Code:" + pResponseCode);

        // If the response code is 302 with a Location header for Login
        // Try to login,if login succeeds attempt to post agin
        if (pResponseCode == HttpURLConnection.HTTP_MOVED_TEMP) {

            //Log.d(TAG, "doPost: POST Attempt response:" + pResponseCode);
            String location = getHeaderValue("Location", pResponse.getHeaderFields());
            //Log.d(TAG, "doPost: POST Redirect URI:" + location);
            if (location.contains(REDIRECT_TO_FORM_LOGIN_URI)) {
                HttpResponse loginResponse = login();
                if (reattemptRequestToUriAfterLogin(uri, loginResponse)) {
                    pResponse = sendMessage(url, "PUT", data, false);
                    //Log.d(TAG, "doPost: POST Reattempt response:" + pResponse.getResponseCode());
                }
            }
        }
        return pResponse;
    }

    /**
     * @param url
     * @return
     * @throws IOException
     * @throws IncompatibleProtocolException
     */
    private HttpResponse getMessage(String url) throws IOException, IncompatibleProtocolException {

        int responseCode;
        String responseBody = null;
        Map<String, List<String>> responseHeaderFields = null;

        HttpURLConnection urlConnection = null;
        try {

            urlConnection = setupConnection(url);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setConnectTimeout(CommunicationService.CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(CommunicationService.READ_TIMEOUT);

            responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                responseBody = read(urlConnection.getInputStream());
            }
            responseHeaderFields = urlConnection.getHeaderFields();
        } finally {
            if (urlConnection != null) {
                responseCode = urlConnection.getResponseCode();
                cleanupConnection(urlConnection);
            }
        }

        //  Log.d(TAG, "getMessage Response Code: " + responseCode);
        //Log.d(TAG, "getMessage Response Body: " + responseBody);
        return new HttpResponse(responseCode, responseBody, responseHeaderFields);
    }


    /**
     * @param url
     * @param data
     * @return
     * @throws IOException
     * @throws IncompatibleProtocolException
     */
    private HttpResponse sendMessage(String url, String method, String data, boolean compress) throws IOException, IncompatibleProtocolException {

        int responseCode;
        String responseBody = null;
        Map<String, List<String>> responseHeaderFields = null;
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        try {
            urlConnection = setupConnection(url);
            urlConnection.setRequestMethod(method);
            urlConnection.setRequestProperty("Content-type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");

            if (data != null && data.length() > 0) {
                urlConnection.setDoOutput(true);

                byte[] bytes = null;
                if (compress == true) {
                    urlConnection.addRequestProperty("Content-Encoding", "gzip");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    GZIPOutputStream gzos = new GZIPOutputStream(baos);
                    gzos.write(data.getBytes(Charset.forName("UTF-8")));
                    gzos.close();
                    bytes = baos.toByteArray();
                } else {
                    bytes = data.getBytes();
                }

                out = new BufferedOutputStream(
                        urlConnection.getOutputStream());
                out.write(bytes);
                out.flush();
            }

            responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                responseBody = read(urlConnection.getInputStream());
            }
            responseHeaderFields = urlConnection.getHeaderFields();

            //    Log.d(TAG, "sendMessage: Location Header in response message:" + urlConnection.getHeaderField("Location"));
            //  Log.d(TAG, "sendMessage responseBody: " + responseBody);
        } finally {

            if (out != null) {
                out.close(); // does not flush the target stream
            }
            if (urlConnection != null) {
                responseCode = urlConnection.getResponseCode();
                cleanupConnection(urlConnection);
            }
        }
        return new HttpResponse(responseCode, responseBody, responseHeaderFields);

    }

    /**
     * @param url
     * @return
     * @throws IOException
     * @throws IncompatibleProtocolException
     */
    private HttpURLConnection setupConnection(String url) throws IOException, IncompatibleProtocolException {

        URL requestUrl = new URL(url);

        HttpURLConnection urlConnection = (HttpURLConnection) requestUrl.openConnection();

        // Do the following for the case of only self-signed certificate support
        // Since commercial certificates are deployed, this condition doesn't arise normally
        if ((sslContext != null) && (urlConnection instanceof HttpsURLConnection)) {
            ((HttpsURLConnection) urlConnection).setSSLSocketFactory(sslContext.getSocketFactory());
        }
        urlConnection.setInstanceFollowRedirects(CommunicationService.DO_NOT_FOLLOW_REDIRECTS);
        return urlConnection;
    }

    /**
     * @param urlConnection
     */
    private void cleanupConnection(HttpURLConnection urlConnection) {

        if (urlConnection != null) {

            try {
                // If there is an exception in earlier getResponseCode
                // get the responseCode again so that caller can get the right response code
                int responseCode = urlConnection.getResponseCode();

                // Clear error message stream, so that connection may get reused
                String errorMsg = read(urlConnection.getErrorStream());
                if (errorMsg != null) Log.d(TAG, errorMsg);
                Log.d(TAG, "cleanupConnection:" + Integer.toString(responseCode));

            } catch (IOException e) {

            }

            urlConnection.disconnect();
        }

    }

    /**
     * @param inputStream
     * @return
     * @throws IOException
     */
    private String read(InputStream inputStream) throws IOException {

        if (inputStream == null) {
            return null;
        }

        String rcvdInput = null;

        BufferedInputStream bufferedInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;

        try {
            bufferedInputStream = new BufferedInputStream(inputStream);
            byteArrayOutputStream = new ByteArrayOutputStream();

            final byte[] buffer = new byte[1024];
            int available = 0;

            while ((available = bufferedInputStream.read(buffer)) >= 0) {
                byteArrayOutputStream.write(buffer, 0, available);
            }

            rcvdInput = byteArrayOutputStream.toString();

        } finally {
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }
        }

        return rcvdInput;
    }


    /**
     * @param uri
     * @param loginResponse
     * @return
     */
    private boolean reattemptRequestToUriAfterLogin(String uri, HttpResponse loginResponse) {

        boolean reattempt = false;

        // Earlier request(GET or PUT) got a 302 response to login  and login was attempted
        // Login flow takes care of redirection to success or failure or redirect to earlier attempted
        // request. Depending on the login response, same request (GET, PUT) can be reattempted
        if (loginResponse.getResponseCode() == HttpURLConnection.HTTP_OK) {
            reattempt = true;
        } else if (loginResponse.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
            String location = getHeaderValue("Location", loginResponse.getHeaderFields());

            if (location != null && location.contains(uri)) {
                reattempt = true;
            }
        }

        return reattempt;
    }

    /**
     * @param key
     * @param headerFields
     * @return
     */
    private String getHeaderValue(String key, Map<String, List<String>> headerFields) {

        String value = null;
        if (headerFields != null) {
            List<String> headerValues = headerFields.get(key);
            if (headerValues != null) {
                value = headerValues.get(0);
            }
        }
        return value;
    }

    /**
     * @param response
     * @return
     * @throws IOException
     * @throws IncompatibleProtocolException
     */
    public HttpResponse redirect(HttpResponse response) throws IOException, IncompatibleProtocolException {

        HttpResponse redirectResponse = null;
        List<String> headerValues = response.getHeaderFields().get("Location");
        String locationUrl = (headerValues != null ? headerValues.get(0) : null);
        if (locationUrl != null) {
            Log.d(TAG, "REDIRECT_URL:" + locationUrl);
            redirectResponse = getMessage(locationUrl);
        }
        return redirectResponse;
    }


    /**
     * @return
     */
    public String buildLoginUrl() {

        String url = null;

        if (authParams != null && serverUrl != null) {

            String slashChar = "";
            if (!serverUrl.endsWith("/")) slashChar = "/";

            url = serverUrl + slashChar + "amcu/j_spring_security_check?j_username=" +
                    authParams.getUsername() + "&j_password=" + authParams.getPassword();

            Log.d(TAG, "In BuildLoginURL ,URL is: " + url);

        }
        return url;
    }

    /**
     * @return
     */
    public String buildLogoutUrl() {

        String url = null;

        if (authParams != null && serverUrl != null) {

            String slashChar = "";
            if (!serverUrl.endsWith("/")) slashChar = "/";
            url = serverUrl + slashChar + "amcu/j_spring_security_logout";
        }
        return url;
    }

}
