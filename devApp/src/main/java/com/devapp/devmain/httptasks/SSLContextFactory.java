package com.devapp.devmain.httptasks;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


/**
 * Created by xxx on 9/4/15.
 */
public class SSLContextFactory {

    private static String TAG = "SSL-CONTEXT-FACTORY";
    private static SSLContextFactory instance = null;

    private SSLContextFactory() {
    }

    public static SSLContextFactory getInstance() {
        if (instance == null) {
            instance = new SSLContextFactory();
        }
        return instance;
    }


    public SSLContext createSSLContext(String certificateString) throws IOException, SSLContextCreationException {

        SSLContext context = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            byte[] der = loadPemCertificate(
                    new ByteArrayInputStream(certificateString.getBytes()));

            ByteArrayInputStream derInputStream = new ByteArrayInputStream(der);

            Certificate ca;
            try {
                ca = cf.generateCertificate(derInputStream);
            } finally {
                derInputStream.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
        } catch (CertificateException cex) {
            //Log.d(TAG, cex.toString());
            throw new SSLContextCreationException(cex.toString());
        } catch (NoSuchAlgorithmException nsx) {
            // Log.d(TAG, nsx.toString());
            throw new SSLContextCreationException(nsx.toString());

        } catch (KeyStoreException kex) {
            // Log.d(TAG, kex.toString());
            throw new SSLContextCreationException(kex.toString());

        } catch (KeyManagementException ex) {
            // Log.d(TAG, ex.toString());
            throw new SSLContextCreationException(ex.toString());
        }

        return context;

    }


    /**
     * Reads and decodes a base-64 encoded DER certificate (a .pem certificate), typically the server's CA cert.
     *
     * @param certificateStream an InputStream from which to read the cert
     * @return a byte[] containing the decoded certificate
     * @throws IOException
     */
    private byte[] loadPemCertificate(InputStream certificateStream) throws IOException {

        byte[] der = null;
        BufferedReader br = null;

        try {
            StringBuilder buf = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(certificateStream));

            String line = br.readLine();
            while (line != null) {
                if (!line.startsWith("--")) {
                    buf.append(line);
                }
                line = br.readLine();
            }

            String pem = buf.toString();
            der = Base64.decode(pem, Base64.DEFAULT);

        } finally {
            if (br != null) {
                br.close();
            }
        }

        return der;
    }
}