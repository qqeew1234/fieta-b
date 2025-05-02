package EtfRecommendService.report.domain;

public enum ReportReason {
    SPAM,
    INAPPROPRIATE_LANGUAGE,
    HARASSMENT,
    ILLEGAL_CONTENT,
    ETC;

    public static ReportReason toEnum(String reportReason) {
        if (reportReason == null) {
            return ETC;
        }

        String normalizedReason = reportReason.trim().toUpperCase().replace(" ", "_");

        try {
            return valueOf(normalizedReason);
        } catch (IllegalArgumentException e) {
            return ETC;
        }
    }
}