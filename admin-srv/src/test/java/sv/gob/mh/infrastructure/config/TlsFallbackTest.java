package sv.gob.mh.infrastructure.config;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * La confianza TLS de respaldo para los certificados internos del Ministerio.
 *
 * <p>Es un punto delicado: el respaldo hace que un certificado que el almacén por defecto no
 * reconoce <b>se acepte igualmente</b>. Lo que lo mantiene acotado es el verificador de nombre,
 * que sólo admite {@code *.mh.gob.sv}. Estas pruebas fijan las dos mitades: que el respaldo no
 * lance, y que el dominio siga siendo la frontera.</p>
 */
class TlsFallbackTest {

    private static final String FUERA_DEL_ALMACEN = "no está en el almacén";
    private static final String NOMBRE_DEL_CERTIFICADO = "servicio.mh.gob.sv";

    private X509Certificate certificadoDePrueba() {
        X509Certificate certificado = mock(X509Certificate.class);
        javax.security.auth.x500.X500Principal principal =
            new javax.security.auth.x500.X500Principal("CN=" + NOMBRE_DEL_CERTIFICADO);
        when(certificado.getSubjectX500Principal()).thenReturn(principal);
        return certificado;
    }

    @Test
    @DisplayName("Un certificado que el almacén reconoce pasa por el camino normal")
    void certificadoConocidoPasaPorElCaminoNormal() throws Exception {
        X509TrustManager porDefecto = mock(X509TrustManager.class);
        X509Certificate[] cadena = { certificadoDePrueba() };

        CompositeTrustManager compuesto = new CompositeTrustManager(porDefecto);
        compuesto.checkServerTrusted(cadena, "RSA");
        compuesto.checkClientTrusted(cadena, "RSA");

        verify(porDefecto).checkServerTrusted(cadena, "RSA");
        verify(porDefecto).checkClientTrusted(cadena, "RSA");
    }

    @Test
    @DisplayName("Un certificado que el almacén NO reconoce se acepta como respaldo, sin lanzar")
    void certificadoDesconocidoSeAceptaComoRespaldo() throws Exception {
        X509TrustManager porDefecto = mock(X509TrustManager.class);
        X509Certificate[] cadena = { certificadoDePrueba() };
        doThrow(new CertificateException(FUERA_DEL_ALMACEN))
            .when(porDefecto).checkServerTrusted(any(), anyString());
        doThrow(new CertificateException(FUERA_DEL_ALMACEN))
            .when(porDefecto).checkClientTrusted(any(), anyString());

        CompositeTrustManager compuesto = new CompositeTrustManager(porDefecto);

        assertDoesNotThrow(() -> compuesto.checkServerTrusted(cadena, "RSA"));
        assertDoesNotThrow(() -> compuesto.checkClientTrusted(cadena, "RSA"));
    }

    @Test
    @DisplayName("Los emisores aceptados son los del almacén por defecto, sin añadidos")
    void losEmisoresSonLosDelAlmacen() throws Exception {
        X509TrustManager porDefecto = mock(X509TrustManager.class);
        X509Certificate[] emisores = { certificadoDePrueba() };
        when(porDefecto.getAcceptedIssuers()).thenReturn(emisores);

        assertSame(emisores, new CompositeTrustManager(porDefecto).getAcceptedIssuers());
    }

    @Test
    @DisplayName("Al inicializar, el verificador de nombre acota el respaldo a *.mh.gob.sv")
    void elVerificadorAcotaElRespaldoAlDominio() {
        HostnameVerifier anterior = HttpsURLConnection.getDefaultHostnameVerifier();
        try {
            new TrustAllSSLInitializer().init();

            HostnameVerifier verificador = HttpsURLConnection.getDefaultHostnameVerifier();
            assertNotNull(verificador);
            assertTrue(verificador.verify("authorization-service.apps.mh.gob.sv", null));
            assertFalse(verificador.verify("un-sitio-cualquiera.com", null));
            assertFalse(verificador.verify(null, null));
        } finally {
            HttpsURLConnection.setDefaultHostnameVerifier(anterior);
        }
    }

    @Test
    @DisplayName("El almacén por defecto de la JVM sí trae un X509TrustManager")
    void elAlmacenPorDefectoTraeUnX509() throws Exception {
        TrustManagerFactory tmf =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init((KeyStore) null);

        boolean hayX509 = false;
        for (TrustManager tm : tmf.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                hayX509 = true;
                break;
            }
        }

        assertTrue(hayX509, "sin él, el respaldo no podría construirse");
    }

    /** Recoge lo que el TrustManager compuesto escribe en el log, que sale a nivel FINE. */
    private static final class Captor extends Handler {
        private final List<String> mensajes = new ArrayList<>();

        @Override
        public void publish(LogRecord registro) {
            mensajes.add(registro.getMessage());
        }

        @Override
        public void flush() {
            // Nada que vaciar: se guarda en memoria.
        }

        @Override
        public void close() {
            // Nada que cerrar.
        }
    }

    @Test
    @DisplayName("El respaldo deja en el log qué certificado aceptó, que es lo único que lo audita")
    void elRespaldoDejaConstanciaDelCertificado() throws Exception {
        X509TrustManager porDefecto = mock(X509TrustManager.class);
        X509Certificate[] cadena = { certificadoDePrueba() };
        doThrow(new CertificateException(FUERA_DEL_ALMACEN))
            .when(porDefecto).checkServerTrusted(any(), anyString());
        doThrow(new CertificateException(FUERA_DEL_ALMACEN))
            .when(porDefecto).checkClientTrusted(any(), anyString());

        Logger log = Logger.getLogger(CompositeTrustManager.class.getName());
        Level anterior = log.getLevel();
        Captor captor = new Captor();
        captor.setLevel(Level.FINE);
        log.setLevel(Level.FINE);
        log.addHandler(captor);
        try {
            CompositeTrustManager compuesto = new CompositeTrustManager(porDefecto);

            compuesto.checkServerTrusted(cadena, "RSA");
            compuesto.checkClientTrusted(cadena, "RSA");
        } finally {
            log.removeHandler(captor);
            log.setLevel(anterior);
        }

        // Aceptar un certificado desconocido sin dejar rastro sería aceptarlo a ciegas.
        assertEquals(2, captor.mensajes.size());
        assertTrue(captor.mensajes.get(0).contains(NOMBRE_DEL_CERTIFICADO));
        assertTrue(captor.mensajes.get(1).contains(NOMBRE_DEL_CERTIFICADO));
    }
}
