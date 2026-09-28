package sv.gob.mh.siip.bdd.support;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import io.cucumber.spring.ScenarioScope;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.domain.Registro;
import sv.gob.mh.siip.model.administracion.dto.ActiveStatusDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogRecordDto;

/**
 * Estado compartido entre step classes de CU-ADM-01 dentro de un mismo escenario BDD, necesario
 * porque varias .feature reutilizan el mismo texto de Dado/Cuando (Cucumber exige una unica
 * definicion por texto en el classpath de test) y esa definicion unica vive en
 * {@code AdmComun}, no en la clase de steps propia de cada .feature:
 * <ul>
 * <li>{@code catalogoActual}/{@code registroActual}: el catalogo/registro que "un catálogo
 * existente" o "un registro existente" (AdmComun) crean y que la clase propia del .feature
 * necesita leer.</li>
 * <li>{@code catalogoResultado}/{@code registroResultado}/{@code activeResultado}: el resultado
 * que un Cuando compartido en AdmComun (p.ej. "fijo su ACTIVE en INACTIVE", que aplica tanto a
 * catalogos como a registros) deja para que el Entonces propio de cada .feature lo verifique.</li>
 * <li>{@code claveConsultada}/{@code camposSolicitados}/{@code resultadoLista}: usados por el
 * Cuando compartido "realizo la búsqueda" (AdmComun), que dispatcha a
 * RegistroService.buscarPorClave (si hay clave) o a buscarLista (si no hay), reutilizado por
 * buscar-lista-registros.feature y buscar-registro-por-clave.feature.</li>
 * </ul>
 * Bean nuevo por escenario (ScenarioScope de cucumber-spring).
 */
@Component
@ScenarioScope
public class ContextoCatalogoBdd {

    private Catalogo catalogoActual;
    private Registro registroActual;
    private CatalogDto catalogoResultado;
    private CatalogRecordDto registroResultado;
    private ActiveStatusDto activeResultado;
    private String claveConsultada;
    private List<String> camposSolicitados;
    private Page<CatalogRecordDto> resultadoLista;

    public Catalogo getCatalogoActual() {
        return catalogoActual;
    }

    public void setCatalogoActual(Catalogo catalogoActual) {
        this.catalogoActual = catalogoActual;
    }

    public Registro getRegistroActual() {
        return registroActual;
    }

    public void setRegistroActual(Registro registroActual) {
        this.registroActual = registroActual;
    }

    public CatalogDto getCatalogoResultado() {
        return catalogoResultado;
    }

    public void setCatalogoResultado(CatalogDto catalogoResultado) {
        this.catalogoResultado = catalogoResultado;
    }

    public CatalogRecordDto getRegistroResultado() {
        return registroResultado;
    }

    public void setRegistroResultado(CatalogRecordDto registroResultado) {
        this.registroResultado = registroResultado;
    }

    public ActiveStatusDto getActiveResultado() {
        return activeResultado;
    }

    public void setActiveResultado(ActiveStatusDto activeResultado) {
        this.activeResultado = activeResultado;
    }

    public String getClaveConsultada() {
        return claveConsultada;
    }

    public void setClaveConsultada(String claveConsultada) {
        this.claveConsultada = claveConsultada;
    }

    public List<String> getCamposSolicitados() {
        return camposSolicitados;
    }

    public void setCamposSolicitados(List<String> camposSolicitados) {
        this.camposSolicitados = camposSolicitados;
    }

    public Page<CatalogRecordDto> getResultadoLista() {
        return resultadoLista;
    }

    public void setResultadoLista(Page<CatalogRecordDto> resultadoLista) {
        this.resultadoLista = resultadoLista;
    }
}
