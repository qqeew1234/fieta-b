package Etf.etf;

import org.springframework.stereotype.Service;

@Service
public class EtfService {

    private final EtfRepository etfRepository;

    public EtfService(EtfRepository etfRepository) {
        this.etfRepository = etfRepository;
    }
}
