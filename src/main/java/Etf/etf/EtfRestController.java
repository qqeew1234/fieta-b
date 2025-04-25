package Etf.etf;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class EtfRestController {

    private final EtfService etfService;

    public EtfRestController(EtfService etfService) {
        this.etfService = etfService;
    }
}
