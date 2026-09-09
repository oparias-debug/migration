package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.*;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.*;
import sv.gob.mh.siip.model.preinversion.domain.*;
import sv.gob.mh.siip.model.preinversion.enums.*;
import sv.gob.mh.siip.model.preinversion.repository.*;
import sv.gob.mh.siip.model.programacion.repository.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BandejaPreinversionIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired UsuarioRepository usuarios;
    @Autowired SolicitudPreinversionRepository solicitudes;
    @Autowired ProyectoRepository proyectos;
    @Autowired InstitucionRepository instituciones;
    @Autowired UnidadEjecutoraRepository unidades;
    @Autowired MacroSectorRepository macros;
    @Autowired SectorActividadRepository sectores;
    @Autowired EjeTematicoRepository ejes;
    Usuario coordinador, tecnico, otro, urp;
    SolicitudPreinversion solicitud;

    @BeforeEach void preparar() {
        String sufijo = UUID.randomUUID().toString().substring(0, 7);
        coordinador = usuario("c" + sufijo, RolUsuario.COORDINADOR_PRE);
        tecnico = usuario("t" + sufijo, RolUsuario.TECNICO_PRE);
        otro = usuario("o" + sufijo, RolUsuario.TECNICO_PRE);
        urp = usuario("u" + sufijo, RolUsuario.TECNICO_URP);
        var institucion = instituciones.save(ProyectoFixtures.nuevaInstitucion(sufijo, "Institución"));
        var unidad = unidades.save(ProyectoFixtures.nuevaUnidadEjecutora(sufijo, "Unidad", institucion));
        var macro = macros.save(ProyectoFixtures.nuevoMacrosector(sufijo, "Macro"));
        var sector = sectores.save(ProyectoFixtures.nuevoSector(sufijo, "Sector", macro));
        var eje = ejes.save(ProyectoFixtures.nuevoEjeTematico(sufijo, "Eje"));
        var proyecto = proyectos.save(ProyectoFixtures.nuevoProyecto("Proyecto prueba", EstadoProyecto.ENVIADO_DGICP_REGISTRO,
                unidad, institucion, sector, eje));
        solicitud = solicitudes.saveAndFlush(SolicitudPreinversion.builder().proyecto(proyecto).estado(EstadoSolicitud.REGISTRADA)
                .tipoSolicitud(TipoSolicitud.CUP).fechaSolicitud(LocalDateTime.now()).tecnicoAsignado(tecnico).build());
    }
    Usuario usuario(String nombre, RolUsuario rol) {
        return usuarios.save(Usuario.builder().nombreUsuario(nombre).nombreCompleto(nombre).rol(rol).activo(true).build());
    }
    @Test void autenticacionYRoles() throws Exception {
        mvc.perform(get("/solicitudes")).andExpect(status().isUnauthorized());
        mvc.perform(get("/solicitudes").header("X-Usuario", urp.getNombreUsuario())).andExpect(status().isForbidden());
        mvc.perform(get("/solicitudes/archivadas").header("X-Usuario", tecnico.getNombreUsuario())).andExpect(status().isForbidden());
        mvc.perform(get("/catalogos/tecnicos-pre").header("X-Usuario", tecnico.getNombreUsuario())).andExpect(status().isForbidden());
        mvc.perform(post("/solicitudes/{id}/archivo", solicitud.getId()).header("X-Usuario", tecnico.getNombreUsuario())).andExpect(status().isForbidden());
        mvc.perform(put("/solicitudes/{id}/asignacion", solicitud.getId()).header("X-Usuario", tecnico.getNombreUsuario())
                .contentType(MediaType.APPLICATION_JSON).content("{\"idTecnicoAsignado\":" + otro.getId() + "}"))
                .andExpect(status().isForbidden());
    }
    @Test void tecnicoSoloVeSusCasos() throws Exception {
        mvc.perform(get("/solicitudes").header("X-Usuario", tecnico.getNombreUsuario()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.contenido[0].idSolicitud").value(solicitud.getId()))
                .andExpect(jsonPath("$.contenido.length()").value(1));
        mvc.perform(get("/solicitudes").header("X-Usuario", otro.getNombreUsuario()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.contenido.length()").value(0));
    }
    @Test void asignarNoCambiaEstadoYRepetirConservaFecha() throws Exception {
        String url = "/solicitudes/" + solicitud.getId() + "/asignacion";
        mvc.perform(put(url).header("X-Usuario", coordinador.getNombreUsuario()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"idTecnicoAsignado\":" + otro.getId() + "}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("ENVIADO_DGICP_REGISTRO"))
                .andExpect(jsonPath("$.asignadoA.idUsuario").value(otro.getId()));
        var fecha = solicitudes.findById(solicitud.getId()).orElseThrow().getFechaAsignacion();
        mvc.perform(put(url).header("X-Usuario", coordinador.getNombreUsuario()).contentType(MediaType.APPLICATION_JSON)
                .content("{\"idTecnicoAsignado\":" + otro.getId() + "}" )).andExpect(status().isOk());
        assertThat(solicitudes.findById(solicitud.getId()).orElseThrow().getFechaAsignacion()).isEqualTo(fecha);
    }
    @Test void archivarRetiraActivaYConservaProyectoYFecha() throws Exception {
        String url = "/solicitudes/" + solicitud.getId() + "/archivo";
        mvc.perform(post(url).header("X-Usuario", coordinador.getNombreUsuario())).andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoSolicitud").value("ARCHIVADA"));
        var fecha = solicitudes.findById(solicitud.getId()).orElseThrow().getFechaArchivo();
        mvc.perform(post(url).header("X-Usuario", coordinador.getNombreUsuario())).andExpect(status().isOk());
        assertThat(solicitudes.findById(solicitud.getId()).orElseThrow().getFechaArchivo()).isEqualTo(fecha);
        assertThat(solicitud.getProyecto().getEstado()).isEqualTo(EstadoProyecto.ENVIADO_DGICP_REGISTRO);
        mvc.perform(get("/solicitudes").header("X-Usuario", tecnico.getNombreUsuario())).andExpect(jsonPath("$.contenido.length()").value(0));
        mvc.perform(get("/solicitudes/archivadas").header("X-Usuario", coordinador.getNombreUsuario()))
                .andExpect(jsonPath("$.contenido[0].idSolicitud").value(solicitud.getId()));
    }
    @Test void emitidasYProyectosSinEnviarNoSonActivos() throws Exception {
        solicitud.setEstado(EstadoSolicitud.APROBADA); solicitudes.saveAndFlush(solicitud);
        mvc.perform(get("/solicitudes").header("X-Usuario", tecnico.getNombreUsuario())).andExpect(jsonPath("$.contenido.length()").value(0));
        solicitud.setEstado(EstadoSolicitud.REGISTRADA); solicitud.getProyecto().setEstado(EstadoProyecto.EN_REGISTRO); solicitudes.saveAndFlush(solicitud);
        mvc.perform(get("/solicitudes").header("X-Usuario", tecnico.getNombreUsuario())).andExpect(jsonPath("$.contenido.length()").value(0));
    }
    @Test void filtroPaginacionYConteosGlobales() throws Exception {
        solicitudes.saveAndFlush(SolicitudPreinversion.builder().proyecto(solicitud.getProyecto()).estado(EstadoSolicitud.REGISTRADA)
                .tipoSolicitud(TipoSolicitud.OPINION_TECNICA).fechaSolicitud(LocalDateTime.now()).tecnicoAsignado(tecnico).build());
        mvc.perform(get("/solicitudes").param("tipoSolicitud", "CUP").param("tamanio", "1").header("X-Usuario", coordinador.getNombreUsuario()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.contenido.length()").value(1))
                .andExpect(jsonPath("$.contenido[0].tipoSolicitud").value("CUP"))
                .andExpect(jsonPath("$.conteoPorTecnico[0].cantidadCup").value(1))
                .andExpect(jsonPath("$.conteoPorTecnico[0].cantidadOpinionTecnica").value(1));
    }
    @Test void entradasInvalidasYRecursosAusentes() throws Exception {
        mvc.perform(put("/solicitudes/{id}/asignacion", solicitud.getId()).header("X-Usuario", coordinador.getNombreUsuario())
                .contentType(MediaType.APPLICATION_JSON).content("{}" )).andExpect(status().isBadRequest());
        mvc.perform(put("/solicitudes/{id}/asignacion", solicitud.getId()).header("X-Usuario", coordinador.getNombreUsuario())
                .contentType(MediaType.APPLICATION_JSON).content("{\"idTecnicoAsignado\":" + urp.getId() + "}" )).andExpect(status().isNotFound());
        mvc.perform(post("/solicitudes/999999/archivo").header("X-Usuario", coordinador.getNombreUsuario())).andExpect(status().isNotFound());
    }
}
