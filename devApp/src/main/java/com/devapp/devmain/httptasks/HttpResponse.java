package com.devapp.devmain.httptasks;

import java.util.List;
import java.util.Map;

/**
 * Created by xxx on 10/5/15.
 */
public class HttpResponse {
    int responseCode;
    String responseBody;
    Map<String, List<String>> headerFields;

    /**
     * @param responseCode
     * @param responseBody
     * @param headerFields
     */
    public HttpResponse(int responseCode, String responseBody, Map<String, List<String>> headerFields) {
        this.responseCode = responseCode;
        this.responseBody = responseBody;
        this.headerFields = headerFields;
    }

    /**
     * @return
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * @param responseCode
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * @return
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * @param responseBody
     */
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    /**
     * @return
     */
    public Map<String, List<String>> getHeaderFields() {
        return headerFields;
    }

    /**
     * @param headerFields
     */
    public void setHeaderFields(Map<String, List<String>> headerFields) {
        this.headerFields = headerFields;
    }
}
