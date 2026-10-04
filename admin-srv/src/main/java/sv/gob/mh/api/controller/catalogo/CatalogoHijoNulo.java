package sv.gob.mh.api.controller.catalogo;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.databind.node.NullNode;

/**
 * SF-12 (RN-17): un catálogo sin hijo responde 200 con el cuerpo JSON {@code null}, como declara
 * el contrato ({@code oneOf: [ChildCatalogResult, null]}). Sin esto, Spring omitiría el cuerpo de
 * un {@code ResponseEntity} nulo.
 */
@RestControllerAdvice(assignableTypes = CatalogosAdministracionController.class)
public class CatalogoHijoNulo implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.getMethod() != null && "consultarCatalogoHijo".equals(returnType.getMethod().getName())
                && AbstractJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
            ServerHttpResponse response) {
        return body == null ? NullNode.getInstance() : body;
    }
}
