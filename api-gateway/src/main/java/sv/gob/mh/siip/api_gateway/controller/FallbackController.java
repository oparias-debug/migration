package sv.gob.mh.siip.api_gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {
    @GetMapping("/fallback/back")
    public String fallbackBack() {
        return """
                Servicio Back no disponible en este momento.
                Por favor intente más tarde.""";
    }
}
