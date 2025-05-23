package EtfRecommendService.webSocket;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvLoader {
    private List<String> codes = new ArrayList<>();

    @PostConstruct
    public void init() {
        String csvPath = "src/main/resources/etf_data_result.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line;
            br.readLine(); // 헤더 건너뛰기
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(",");
                codes.add(cols[0].trim());
            }
            System.out.println("CSV 로딩 완료, 총 종목 개수: " + codes.size());
        } catch (Exception e) {
            e.printStackTrace();
            // 필요하면 예외 처리 강화
        }
    }

    public List<String> getCodes() {
        return codes;
    }

    public int getCount() {
        return codes.size();
    }
}
