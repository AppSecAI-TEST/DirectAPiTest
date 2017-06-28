package api.vdp.visa.com.vdpandroidvisadirecttest;

import android.content.Context;
import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.CertificatePinner;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

public final class ConnectionFactory {
    private final OkHttpClient client;
    private final Context context;



    public ConnectionFactory(Context context) {
        this.context = context;
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                return myTrustedAnchors;
            }
        };
        SSLSocketFactory sslSocketFactory;
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                return myTrustedAnchors;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        } };
        try {



            //trustManager = trustManagerForCertificates(trustedCertificatesInputStream());
            SSLContext sslContext = SSLContext.getInstance("TLSv1");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        client = new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustManager)
                .build();

    }

    private static String getBasicAuthHeader(String userId, String password) {
        return "Basic " + base64Encode(userId + ":" + password);
    }

    public static String base64Encode(String token) {
        byte[] encodedBytes = Base64.encode(token.getBytes(),Base64.NO_WRAP);
        return new String(encodedBytes, Charset.forName("UTF-8"));
    }

    private static String USER_ID = "TZNRHY1UAOWWFX5YP4D521IrJPpDJ51s4eMlJK0FCWReMXBDg"; //-for qaperf
    private static String PASSWORD = "lvB5CuZ02ki63ZB59wFHu3Bgutck6";

    public void run() throws Exception {



        Request request = new Request.Builder()
                .url("https://sandbox.api.visa.com/vdp/helloworld")
                .header("Accept","application/json")
                .addHeader("Content-Type","application/json")
                .addHeader("Authorization",getBasicAuthHeader(USER_ID, PASSWORD))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            Headers responseHeaders = response.headers();
            for (int i = 0; i < responseHeaders.size(); i++) {
                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
            }

            System.out.println(response.body().string());
        }
    }

    /**
     * Returns an input stream containing one or more certificate PEM files. This implementation just
     * embeds the PEM files in Java strings; most applications will instead read this from a resource
     * file that gets bundled with the application.
     */
    private InputStream trustedCertificatesInputStream() {
        // PEM files for root certificates of Comodo and Entrust. These two CAs are sufficient to view
        // https://publicobject.com (Comodo) and https://squareup.com (Entrust). But they aren't
        // sufficient to connect to most HTTPS sites including https://godaddy.com and https://visa.com.
        // Typically developers will need to get a PEM file from their organization's TLS administrator.
        String comodoRsaCertificationAuthority ="-----BEGIN CERTIFICATE-----\n"+
                "MIIDRzCCAi+gAwIBAgIILR7Slflq2XowDQYJKoZIhvcNAQELBQAwMTEOMAwGA1UE\n"+
                "AwwFVkRQQ0ExEjAQBgNVBAoMCVZEUFZJU0FDQTELMAkGA1UEBhMCVVMwHhcNMTUw\n"+
                "NzI0MDQyNzM3WhcNMjUwNzIxMDQyNzM3WjAxMQ4wDAYDVQQDDAVWRFBDQTESMBAG\n"+
                "A1UECgwJVkRQVklTQUNBMQswCQYDVQQGEwJVUzCCASIwDQYJKoZIhvcNAQEBBQAD\n"+
                "ggEPADCCAQoCggEBALBCS2GpaICzh0f/khGWc4pXbgPk8o90fnOIMZFOVdtmDs5t\n"+
                "AwgXwz6SIPJXANMdetxEK5T/PYik4UNnlkt0mWUzcfH4wfQ/kRP3Kxi9Xh3jOPQV\n"+
                "+2SiyBLdzupuULXYAXpmHw3CYedxtAUmuUEtUpplcxS7hLeylFDqz/1Rj0Zw4ncf\n"+
                "lVVOCV4y7KIv4WuXuAv8rR/pVgmI/n897pvgKpkZREBYYvcOjeOzDIUMlmER03TE\n"+
                "hGs8p8SkcNCfscGSl6tLHbph0MrAjkgOKAdYt45Np/VApHUSpPs7OPof68DaHvJq\n"+
                "trNYSz+TELLVLUJhUIxuf+hMibfAvl6mDbsMXB0CAwEAAaNjMGEwHQYDVR0OBBYE\n"+
                "FK/dbragS5x5uRYIYuYjMRCnguuhMA8GA1UdEwEB/wQFMAMBAf8wHwYDVR0jBBgw\n"+
                "FoAUr91utqBLnHm5Fghi5iMxEKeC66EwDgYDVR0PAQH/BAQDAgGGMA0GCSqGSIb3\n"+
                "DQEBCwUAA4IBAQAn1V7dAa3F8/7ZRJNOXhkRxOq6Ol6esQDWz+GW310Dw+S+v/PJ\n"+
                "YBp6xcNqx1qPVZUpGncLRgxs11O2FkD7KttWR5WhflBU/SMZAHYCGv9npO/d9mxF\n"+
                "tGw0KiswbQ+5aJFTbTS0m8d3t86VWurDTI+rSm5lfje41NW5fhaWZFb+tzdBr0Y+\n"+
                "ELsJWc3O3n26713WvaXNh40KmH+H6EZhZXUXAL0kqX/hMfxL7ejNFlVfteQHNPiH\n"+
                "z5gk7SbVtikwrbNmHRi0gEMsp0cvWq3sjsSetFtkHVTwP2gmNjCt7Jn9w7Uw4je4\n"+
                "XBcH+yvmUByeIF0pj9lZP+0ktQxio0tJU6tQ\n"+
                "-----END CERTIFICATE-----\n";
        String entrustRootCertificateAuthority = "-----BEGIN CERTIFICATE-----\n"+
                "MIIDRzCCAi+gAwIBAgIILR7Slflq2XowDQYJKoZIhvcNAQELBQAwMTEOMAwGA1UE\n"+
        "AwwFVkRQQ0ExEjAQBgNVBAoMCVZEUFZJU0FDQTELMAkGA1UEBhMCVVMwHhcNMTUw\n"+
        "NzI0MDQyNzM3WhcNMjUwNzIxMDQyNzM3WjAxMQ4wDAYDVQQDDAVWRFBDQTESMBAG\n"+
                "A1UECgwJVkRQVklTQUNBMQswCQYDVQQGEwJVUzCCASIwDQYJKoZIhvcNAQEBBQAD\n"+
                "ggEPADCCAQoCggEBALBCS2GpaICzh0f/khGWc4pXbgPk8o90fnOIMZFOVdtmDs5t\n"+
                "AwgXwz6SIPJXANMdetxEK5T/PYik4UNnlkt0mWUzcfH4wfQ/kRP3Kxi9Xh3jOPQV\n"+
                "+2SiyBLdzupuULXYAXpmHw3CYedxtAUmuUEtUpplcxS7hLeylFDqz/1Rj0Zw4ncf\n"+
                "lVVOCV4y7KIv4WuXuAv8rR/pVgmI/n897pvgKpkZREBYYvcOjeOzDIUMlmER03TE\n"+
                "hGs8p8SkcNCfscGSl6tLHbph0MrAjkgOKAdYt45Np/VApHUSpPs7OPof68DaHvJq\n"+
                "trNYSz+TELLVLUJhUIxuf+hMibfAvl6mDbsMXB0CAwEAAaNjMGEwHQYDVR0OBBYE\n"+
                "FK/dbragS5x5uRYIYuYjMRCnguuhMA8GA1UdEwEB/wQFMAMBAf8wHwYDVR0jBBgw\n"+
                "FoAUr91utqBLnHm5Fghi5iMxEKeC66EwDgYDVR0PAQH/BAQDAgGGMA0GCSqGSIb3\n"+
                "DQEBCwUAA4IBAQAn1V7dAa3F8/7ZRJNOXhkRxOq6Ol6esQDWz+GW310Dw+S+v/PJ\n"+
                "YBp6xcNqx1qPVZUpGncLRgxs11O2FkD7KttWR5WhflBU/SMZAHYCGv9npO/d9mxF\n"+
                "tGw0KiswbQ+5aJFTbTS0m8d3t86VWurDTI+rSm5lfje41NW5fhaWZFb+tzdBr0Y+\n"+
                "ELsJWc3O3n26713WvaXNh40KmH+H6EZhZXUXAL0kqX/hMfxL7ejNFlVfteQHNPiH\n"+
                "z5gk7SbVtikwrbNmHRi0gEMsp0cvWq3sjsSetFtkHVTwP2gmNjCt7Jn9w7Uw4je4\n"+
                "XBcH+yvmUByeIF0pj9lZP+0ktQxio0tJU6tQ\n"+
                "-----END CERTIFICATE-----\n";
        return new Buffer()
                .writeUtf8(comodoRsaCertificationAuthority)
                .writeUtf8(entrustRootCertificateAuthority)
                .inputStream();
    }

    /**
     * Returns a trust manager that trusts {@code certificates} and none other. HTTPS services whose
     * certificates have not been signed by these certificates will fail with a {@code
     * SSLHandshakeException}.
     *
     * <p>This can be used to replace the host platform's built-in trusted certificates with a custom
     * set. This is useful in development where certificate authority-trusted certificates aren't
     * available. Or in production, to avoid reliance on third-party certificate authorities.
     *
     * <p>See also {@link CertificatePinner}, which can limit trusted certificates while still using
     * the host platform's built-in trust store.
     *
     * <h3>Warning: Customizing Trusted Certificates is Dangerous!</h3>
     *
     * <p>Relying on your own trusted certificates limits your server team's ability to update their
     * TLS certificates. By installing a specific set of trusted certificates, you take on additional
     * operational complexity and limit your ability to migrate between certificate authorities. Do
     * not use custom trusted certificates in production without the blessing of your server's TLS
     * administrator.
     */
    private X509TrustManager trustManagerForCertificates(InputStream in)
            throws GeneralSecurityException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }

        // Put the certificates a key store.
        char[] password = "password".toCharArray(); // Any password will work.
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        for (Certificate certificate : certificates) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }

        // Use it to build an X509 trust manager.
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }

    private KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream fis = context.getResources().openRawResource(R.raw.keystore);
            keyStore.load(fis, password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

}