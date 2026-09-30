package sv.gob.mh.application.query.catalogo;

import java.util.List;

import sv.gob.mh.domain.model.catalogo.CampoDefinicion;
import sv.gob.mh.domain.model.catalogo.Registro;

/** Un registro junto con los campos pedidos para su respuesta, en el orden pedido (Reglas 4 y 5). */
public record RegistroProyectado(Registro registro, List<CampoDefinicion> campos) {

    public RegistroProyectado {
        campos = List.copyOf(campos);
    }
}
