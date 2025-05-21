package EtfRecommendService.etf;

public enum Theme {
    USA("미국"),
    KOREA("한국"),
    REITS("리츠 (부동산)"),
    MULTI_ASSET("멀티에셋"),
    COMMODITIES("원자재/실물"),
    HIGH_RISK("고위험"),
    SECTOR("산업별/섹터"),
    DIVIDEND("배당형"),
    ESG("ESG"),
    AI_DATA("AI·데이터"),
    GOLD("금"),
    GOVERNMENT_BOND("국채"),
    CORPORATE_BOND("회사채"),
    DEFENSE("방위"),
    SEMICONDUCTOR("반도체"),
    BIO("바이오"),
    EMERGING_MARKETS("신흥국");

    private final String displayName;

    Theme(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // displayName으로 Theme을 반환하는 static 메서드
    public static Theme fromDisplayName(String displayName) {
        for (Theme theme : Theme.values()) {
            if (theme.getDisplayName().equals(displayName)) {
                return theme;
            }
        }
        throw new IllegalArgumentException("해당 displayName을 가진 Theme이 존재하지 않습니다: " + displayName);
    }
}