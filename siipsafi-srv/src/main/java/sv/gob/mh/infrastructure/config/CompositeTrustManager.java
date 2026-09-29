/*
 * Copyright (c) Ministerio de Hacienda de El Salvador.
 * All rights reserved.
 */
package sv.gob.mh.infrastructure.config;

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TrustManager compuesto: primero intenta la validación estándar del JVM;
 * si falla (certificado autofirmado), acepta como fallback para el entorno interno.
 */
class CompositeTrustManager implements X509TrustManager {

    private static final Logger LOG = Logger.getLogger(CompositeTrustManager.class.getName());

    private final X509TrustManager defaultTrustManager;

    CompositeTrustManager(X509TrustManager defaultTrustManager) {
        this.defaultTrustManager = defaultTrustManager;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return defaultTrustManager.getAcceptedIssuers();
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            defaultTrustManager.checkClientTrusted(chain, authType);
        } catch (CertificateException e) {
            LOG.log(Level.FINE, e, () -> "Client cert not in default TrustStore, accepted as fallback: "
                + chain[0].getSubjectX500Principal().getName());
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            defaultTrustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException e) {
            LOG.log(Level.FINE, e, () -> "Server cert not in default TrustStore, accepted as fallback: "
                + chain[0].getSubjectX500Principal().getName());
        }
    }
}
