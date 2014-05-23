package org.jibble.pircbot;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class TrustingSSLSocketFactory extends SSLSocketFactory {

    private SSLSocketFactory factory;
    private String[] ciphers;

    @SuppressWarnings("restriction")
    public TrustingSSLSocketFactory() throws SSLException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        try {
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("SSLv3");
            sslContext.init(null, new TrustManager[]{new TrustingX509TrustManager()}, null);
            factory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException nsae) {
            throw new SSLException("Unable to initialize the SSL context:  ", nsae);
        } catch (KeyManagementException kme) {
            throw new SSLException("Unable to register a trust manager:  ", kme);
        }
        ciphers = factory.getDefaultCipherSuites();
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return ciphers;
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return ciphers;
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return prepare((SSLSocket) factory.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return prepare((SSLSocket) factory.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress address, int port) throws IOException {
        return prepare((SSLSocket) factory.createSocket(address, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return prepare((SSLSocket) factory.createSocket(address, port, localAddress, localPort));
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return prepare((SSLSocket) factory.createSocket(s, host, port, autoClose));
    }

    private SSLSocket prepare(SSLSocket baseSocket) {
        baseSocket.setEnabledCipherSuites(ciphers);
        return baseSocket;
    }

    private static class TrustingX509TrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            // no Exception implies acceptance
        }

        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            // no Exception implies acceptance
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
