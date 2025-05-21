package EtfRecommendService.ai;

import EtfRecommendService.ai.dto.RecommendationResponseDTO;
import EtfRecommendService.ai.dto.UserAnswerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class AIController {
    private final VertexAiGeminiChatModel vertexAiGeminiChatModel;
    private final AIService aiService;

    @PostMapping("/chat")
    public Map<String, String> chat(@RequestBody String message) {
        InputStream is = getClass().getClassLoader().getResourceAsStream("etfrecommendserviceai.json");
        System.out.println(is != null ? "파일 찾음" : "파일 못 찾음");

        Map<String, String> responses = new HashMap<>();

        // AI에게 금융과 주식 관련 전문가로서 대답하도록 지시하는 프롬프트 생성
        String prompt = "당신은 주식 시장과 금융 분석에 전문 지식을 가진 금융 어드바이저입니다. 금융과 주식 외에 다른 질문에 대해서는 '죄송합니다. 금융과 주식과 관련된 이야기만 할 수 있습니다.' 라고 전달하세요."
                + message;

        String vertexAiGeminiResponse = vertexAiGeminiChatModel.call(prompt);
        responses.put("vertexai(gemini) 응답", vertexAiGeminiResponse);
        return responses;
    }

    @PostMapping("/recommendation")
    public RecommendationResponseDTO getRecommendation(@RequestBody List<UserAnswerDTO> userAnswers) {
        return aiService.generateRecommendation(userAnswers);
    }

}