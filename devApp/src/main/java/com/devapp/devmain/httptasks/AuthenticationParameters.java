package com.devapp.devmain.httptasks;

/**
 * Created by xxx on 9/4/15.
 */
public class AuthenticationParameters {

    // Self Signed Server Certificate in PEM format
    private String certificate = null;
    private String username = null;
    private String password = null;

    public AuthenticationParameters(String certificate, String username, String password) {
        this.certificate = certificate;
        this.username = username;
        this.password = password;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}