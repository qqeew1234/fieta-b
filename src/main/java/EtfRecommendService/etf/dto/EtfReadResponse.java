package EtfRecommendService.etf.dto;

import EtfRecommendService.etf.domain.Etf;

public record EtfReadResponse(
        Long etfId,
        String etfName,
        String etfCode
) {
//    public static EtfReadResponse from(Etf etf){
//        return new EtfReadResponse(
//                etf.getId(),
//                etf.getEtfName(),
//                etf.getEtfCode()
//        );
//    }
}
