package EtfRecommendService.report.domain;

public enum ReportType {
    COMMENT,
    REPLY;
    public static ReportType toEnum(String type){
        String normalizedType = type.trim().toUpperCase();
        return valueOf(type);
    }
}
