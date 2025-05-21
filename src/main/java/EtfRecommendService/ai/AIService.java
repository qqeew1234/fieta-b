package EtfRecommendService.ai;

import EtfRecommendService.ai.dto.RecommendationDTO;
import EtfRecommendService.ai.dto.RecommendationResponseDTO;
import EtfRecommendService.ai.dto.UserAnswerDTO;
import EtfRecommendService.etf.EtfQueryRepository;
import EtfRecommendService.etf.EtfService;
import EtfRecommendService.etf.Theme;
import EtfRecommendService.etf.dto.EtfReadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AIService {
    private final VertexAiGeminiChatModel vertexAiGeminiChatModel;
    private static final Map<String, Map<String, String>> questionAnswerMapping = new HashMap<>();
    private final List<String> availableThemes = Arrays.asList(
            "미국", "한국", "리츠 (부동산)", "멀티에셋", "원자재/실물", "고위험",
            "산업별/섹터", "배당형", "ESG", "AI·데이터", "금", "국채",
            "회사채", "방위", "반도체", "바이오", "신흥국"
    );
    private final EtfService etfService;
    //질문 리스트에 대한 답 해석
    static {
        // 1. 왜 투자하려고 하나요?
        Map<String, String> q1 = new HashMap<>();
        q1.put("가", "노후 대비를 위해 투자하고 싶다");
        q1.put("나", "짧게 사고팔아 이익을 얻고 싶다");
        q1.put("다", "매달 나오는 배당을 받고 싶다");
        q1.put("라", "자녀 교육비를 마련하고 싶다");
        q1.put("마", "비상금처럼 보유하고 싶다");
        questionAnswerMapping.put("1", q1);

        // 2. 얼마나 오래 넣어둘 계획인가요?
        Map<String, String> q2 = new HashMap<>();
        q2.put("가", "6개월 이하로 투자할 계획이다");
        q2.put("나", "6개월에서 1년 정도 투자할 계획이다");
        q2.put("다", "1년에서 3년 정도 투자할 계획이다");
        q2.put("라", "3년에서 5년 정도 투자할 계획이다");
        q2.put("마", "5년 이상 장기 투자할 계획이다");
        questionAnswerMapping.put("2", q2);

        // 3. 얼마를 투자할 수 있나요?
        Map<String, String> q3 = new HashMap<>();
        q3.put("가", "투자 금액은 1천만 원 미만이다");
        q3.put("나", "투자 금액은 1천만 ~ 3천만 원이다");
        q3.put("다", "투자 금액은 3천만 ~ 5천만 원이다");
        q3.put("라", "투자 금액은 5천만 ~ 1억 원이다");
        q3.put("마", "투자 금액은 1억 원 이상이다");
        questionAnswerMapping.put("3", q3);

        // 4. 가격이 오르내릴 때 기분이 어떤가요?
        Map<String, String> q4 = new HashMap<>();
        q4.put("가", "가격 변동에 거의 신경 쓰지 않는다");
        q4.put("나", "가격 변동이 조금 걱정된다");
        q4.put("다", "가격 변동에 대해 보통이다");
        q4.put("라", "가격 변동에 좀 불안해한다");
        q4.put("마", "가격 변동에 많이 불안해한다");
        questionAnswerMapping.put("4", q4);

        // 5. 투자해 본 경험이 있나요?
        Map<String, String> q5 = new HashMap<>();
        q5.put("가", "투자 경험이 전혀 없다");
        q5.put("나", "투자 경험이 1년 미만이다");
        q5.put("다", "투자 경험이 1~3년이다");
        q5.put("라", "투자 경험이 3~5년이다");
        q5.put("마", "투자 경험이 5년 이상이다");
        questionAnswerMapping.put("5", q5);

        // 6. 연 수익을 어느 정도 기대하나요?
        Map<String, String> q6 = new HashMap<>();
        q6.put("가", "연 수익 기대는 0~3%이다");
        q6.put("나", "연 수익 기대는 3~5%이다");
        q6.put("다", "연 수익 기대는 5~8%이다");
        q6.put("라", "연 수익 기대는 8~12%이다");
        q6.put("마", "연 수익 기대는 12% 이상이다");
        questionAnswerMapping.put("6", q6);

        // 7. 어떤 종류 ETF를 원하나요?
        Map<String, String> q7 = new HashMap<>();
        q7.put("가", "주식 위주 ETF를 선호한다");
        q7.put("나", "채권 위주 ETF를 선호한다");
        q7.put("다", "주식과 채권이 섞인 ETF를 선호한다");
        q7.put("라", "금, 은 같은 원자재 ETF를 선호한다");
        q7.put("마", "부동산 리츠 ETF를 선호한다");
        questionAnswerMapping.put("7", q7);

        // 8. 어느 나라나 분야에 관심 있나요?
        Map<String, String> q8 = new HashMap<>();
        q8.put("가", "국내 ETF에 관심이 있다");
        q8.put("나", "미국 및 선진국 ETF에 관심이 있다");
        q8.put("다", "신흥국 ETF에 관심이 있다");
        q8.put("라", "IT, 헬스케어 산업 ETF에 관심이 있다");
        q8.put("마", "친환경, 테마형 ETF에 관심이 있다");
        questionAnswerMapping.put("8", q8);

        // 9. 착한 투자(환경·사회)에 관심이 어느 정도인가요?
        Map<String, String> q9 = new HashMap<>();
        q9.put("가", "착한 투자에는 전혀 관심이 없다");
        q9.put("나", "착한 투자에 조금 관심이 있다");
        q9.put("다", "착한 투자에 보통 관심이 있다");
        q9.put("라", "착한 투자에 꽤 관심이 있다");
        q9.put("마", "착한 투자를 꼭 하고 싶다");
        questionAnswerMapping.put("9", q9);

        // 10. 언제든 사고팔고 싶은가요, 오래 두고 싶은가요?
        Map<String, String> q10 = new HashMap<>();
        q10.put("가", "자주 사고팔고 싶다");
        q10.put("나", "한 달에 한 번 정도 사고팔고 싶다");
        q10.put("다", "세 달에 한 번 정도 사고팔고 싶다");
        q10.put("라", "여섯 달에 한 번 정도 사고팔고 싶다");
        q10.put("마", "일 년에 한 번 이하로 사고팔고 싶다");
        questionAnswerMapping.put("10", q10);
    }

    public RecommendationResponseDTO generateRecommendation(List<UserAnswerDTO> userAnswers) {
        // 사용자 질문 답변 문장으로 변환
        String userPreferences = buildUserPreferencesText(userAnswers);

        // 프롬프트 생성
        String prompt = buildRecommendationPrompt(userPreferences, availableThemes);

        String aiResponse = vertexAiGeminiChatModel.call(prompt);

        RecommendationDTO recommendation = parseRecommendation(aiResponse);

        List<String> recommendedThemes = List.of(recommendation.mainRecommendation(),recommendation.subRecommendations().get(0), recommendation.subRecommendations().get(1));

        List<EtfReadResponse> etfReadResponseList = new ArrayList<>();

        for (String theme : recommendedThemes) {
            etfReadResponseList.add(etfService.findTopByThemeOrderByWeeklyReturn(Theme.fromDisplayName(theme)));
        }

        for (EtfReadResponse etfReadResponse : etfReadResponseList) {
            System.out.println(etfReadResponse.toString());
        }

        return new RecommendationResponseDTO(
                ResponseEntity.status(HttpStatus.OK).toString(),
                recommendation,
                etfReadResponseList
            );
    }

    private RecommendationDTO parseRecommendation(String aiResponse) {
        String mainRecommendation = null;
        List<String> subRecommendations = new ArrayList<>();
        String reason = null;

        try {
            // 정규식을 사용하여 응답에서 메인 추천, 보조 추천, 추천 이유 추출
            Pattern mainPattern = Pattern.compile("메인 추천:\\s*(.+)");
            Pattern subPattern = Pattern.compile("보조 추천:\\s*(.+)");
            Pattern reasonPattern = Pattern.compile("추천 이유:\\s*(.+)");

            Matcher mainMatcher = mainPattern.matcher(aiResponse);
            Matcher subMatcher = subPattern.matcher(aiResponse);
            Matcher reasonMatcher = reasonPattern.matcher(aiResponse);

            if (mainMatcher.find()) {
                mainRecommendation = mainMatcher.group(1).trim();
            }

            if (subMatcher.find()) {
                String subRecommendationsText = subMatcher.group(1).trim();
                subRecommendations = Arrays.stream(subRecommendationsText.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
            }

            if (reasonMatcher.find()) {
                reason = reasonMatcher.group(1).trim();
            }

        } catch (Exception e) {
            log.error("응답 파싱 중 오류 발생: {}", e.getMessage());
        }

        // 패턴에 맞지 않는 응답 처리
        if (mainRecommendation == null) {
            mainRecommendation = "파싱 실패";
        }

        if (subRecommendations.isEmpty()) {
            subRecommendations = List.of("파싱 실패");
        }

        if (reason == null) {
            reason = "AI 응답 형식이 예상과 다릅니다";
        }

        return new RecommendationDTO(mainRecommendation, subRecommendations, reason);
    }


    public String buildUserPreferencesText(List<UserAnswerDTO> userAnswers) {
        StringBuilder resultText = new StringBuilder();

        // 사용자가 제출한 답변 리스트를 바탕으로 문장을 생성
        for (UserAnswerDTO userAnswer : userAnswers) {
            String questionNum = userAnswer.question();
            String answerCode = userAnswer.answer();

            // questionAnswerMapping에서 해당 답변에 대한 문장 가져오기
            String sentence = questionAnswerMapping.getOrDefault(questionNum, new HashMap<>()).get(answerCode);
            if (sentence != null) {
                resultText.append(sentence).append(", ");
            } else {
                resultText.append("알 수 없는 답변입니다. ");
            }
        }

        // 최종적으로 구성된 문장에서 마지막 쉼표와 공백을 제거
        String finalText = resultText.length() > 0 ? resultText.substring(0, resultText.length() - 2) : "";

        return finalText;
    }


    private static String buildRecommendationPrompt(String userPreferences, List<String> availableThemes) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("당신은 투자 테마 추천 전문가입니다. 다음 투자 성향을 가진 사용자에게 가장 적합한 ETF 테마를 추천해주세요.\n\n")
                .append("사용자 투자 성향:\n")
                .append(userPreferences)
                .append("\n\n")
                .append("다음 테마 목록에서만 선택하여 추천해주세요:\n");

        // 테마 목록 추가
        for (String theme : availableThemes) {
            prompt.append(theme).append("\n");
        }

        prompt.append("\n")
                .append("아래 양식으로 간결하게 답변해주세요:\n")
                .append("메인 추천: [가장 적합한 테마 1개]\n")
                .append("보조 추천: [보완할 수 있는 테마 2개]\n")
                .append("추천 이유: [30자 이내 간략한 설명]\n\n")
                .append("※ 위 양식을 정확히 지켜주세요.\n")
                .append("※ 테마는 반드시 제공된 목록에서만 선택해주세요.");

        return prompt.toString();
    }

}
