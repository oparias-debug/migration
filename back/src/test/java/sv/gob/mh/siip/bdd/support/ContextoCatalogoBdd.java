package sv.gob.mh.siip.bdd.support;

import org.springframework.stereotype.Component;

import io.cucumber.spring.ScenarioScope;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.domain.Registro;
import sv.gob.mh.siip.model.administracion.dto.CatalogoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.ExistenciaCatalogoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.RegistroResponseDto;

/**
 * Estado compartido entre step classes de CU-ADM-01 dentro de un mismo escenario BDD: el catalogo
 * que un Dado de una clase crea y que otra clase necesita leer (p.ej. "selecciona un catálogo
 * existente" vive en AdmActualizarCatalogo porque tambien lo usa
 * CU-ADM-01-crear-registro.feature), y el resultado/modo de la condicion generica "{string}" de
 * los esquemas del escenario compartidos entre buscar-catalogo-por-nombre, crear-catalogo y
 * crear-registro. Bean nuevo por escenario (ScenarioScope de cucumber-spring).
 */
@Component
@ScenarioScope
public class ContextoCatalogoBdd {

    /** Distingue el significado de la condicion generica "{string}" segun el .feature que la use. */
    public enum ModoCondicion {
        EXISTENCIA_POR_NOMBRE,
        VIGENCIA_CATALOGO,
        VIGENCIA_REGISTRO
    }

    private Catalogo catalogoActual;
    private Registro registroActual;
    private ModoCondicion modoCondicion;
    private String nombreConsultado;
    private CatalogoResponseDto catalogoResultado;
    private RegistroResponseDto registroResultado;
    private ExistenciaCatalogoResponseDto existenciaResultado;

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

    public ModoCondicion getModoCondicion() {
        return modoCondicion;
    }

    public void setModoCondicion(ModoCondicion modoCondicion) {
        this.modoCondicion = modoCondicion;
    }

    public String getNombreConsultado() {
        return nombreConsultado;
    }

    public void setNombreConsultado(String nombreConsultado) {
        this.nombreConsultado = nombreConsultado;
    }

    public CatalogoResponseDto getCatalogoResultado() {
        return catalogoResultado;
    }

    public void setCatalogoResultado(CatalogoResponseDto catalogoResultado) {
        this.catalogoResultado = catalogoResultado;
    }

    public RegistroResponseDto getRegistroResultado() {
        return registroResultado;
    }

    public void setRegistroResultado(RegistroResponseDto registroResultado) {
        this.registroResultado = registroResultado;
    }

    public ExistenciaCatalogoResponseDto getExistenciaResultado() {
        return existenciaResultado;
    }

    public void setExistenciaResultado(ExistenciaCatalogoResponseDto existenciaResultado) {
        this.existenciaResultado = existenciaResultado;
    }
}
