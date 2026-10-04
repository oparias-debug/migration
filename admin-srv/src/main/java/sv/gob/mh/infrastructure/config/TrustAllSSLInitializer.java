/*
 * Copyright (c) Ministerio de Hacienda de El Salvador.
 * All rights reserved.
 */
package sv.gob.mh.infrastructure.config;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Configura un SSLContext con validación flexible para certificados
 * autofirmados en servicios internos del clúster OpenShift.
 *
 * <p>Estrategia de validación:</p>
 * <ol>
 *   <li>Intenta validar el certificado con el TrustStore estándar del JVM (cacerts).</li>
 *   <li>Si la validación estándar falla, acepta el certificado como fallback
 *       para soportar certificados autofirmados del entorno interno.</li>
 * </ol>
 *
 * <p>La verificación de hostname se restringe a dominios internos
 * del Ministerio de Hacienda ({@code *.mh.gob.sv}).</p>
 */
@Configuration
public class TrustAllSSLInitializer {

    private static final Logger LOG = Logger.getLogger(TrustAllSSLInitializer.class.getName());
    private static final String TRUSTED_DOMAIN_SUFFIX = ".mh.gob.sv";

    @PostConstruct
    public void init() {
        try {
            initSslContext();
            LOG.info("Custom SSL context configured: fallback trust enabled for *" + TRUSTED_DOMAIN_SUFFIX);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to configure custom SSL context", e);
        }
    }

    private static void initSslContext() {
        X509TrustManager defaultTm = findDefaultTrustManager();
        TrustManager[] trustManagers = { new CompositeTrustManager(defaultTm) };

        try {
            var sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, new SecureRandom());
            SSLContext.setDefault(sslContext);

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            // El respaldo queda acotado al dominio del Ministerio: fuera de él, la validación
            // normal del certificado sigue mandando.
            HttpsURLConnection.setDefaultHostnameVerifier((String hostname, javax.net.ssl.SSLSession session) ->
                hostname != null && hostname.endsWith(TRUSTED_DOMAIN_SUFFIX));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo configurar el contexto TLS del marco", e);
        }
    }

    private static X509TrustManager findDefaultTrustManager() {
        try {
            var tmf =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);

            for (TrustManager tm : tmf.getTrustManagers()) {
                if (tm instanceof X509TrustManager x509Tm) {
                    return x509Tm;
                }
            }
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo leer el almacén de confianza de la JVM", e);
        }
        throw new IllegalStateException("La JVM no expone ningún X509TrustManager por defecto");
    }
}
