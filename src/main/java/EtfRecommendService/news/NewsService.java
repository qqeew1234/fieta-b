package EtfRecommendService.news;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Slf4j
public class NewsService {

    private final NewsRepository newsRepository;

    @PostConstruct
    public void init() {
        log.info("서버 시작 시 뉴스 스크립트 실행");
        executePythonScript(); // 서버 시작 시 최초 1회 실행
    }

    @Scheduled(cron = "0 0 6 * * ?") // 매일 오전 6시에 실행
    public void executePythonScript() {
        log.info("뉴스 크롤링 스케줄러 실행 시작");
        try {
            // 파이썬 스크립트 파일 추출
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("scripts/news.py");
            if (inputStream == null) {
                log.error("스크립트 파일을 찾을 수 없습니다");
                return;
            }

            // 임시 파일 생성 및 실행
            Path tempFile = Files.createTempFile("news_script", ".py");
            Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
            inputStream.close();
            log.debug("임시 파일 생성: {}", tempFile);

            // 스크립트 실행 및 결과 수집
            ProcessBuilder processBuilder = new ProcessBuilder("python", tempFile.toString());
            processBuilder.redirectErrorStream(false); // 에러 스트림과 출력 스트림 분리
            Process process = processBuilder.start();
            log.debug("파이썬 프로세스 시작됨");

            // 표준 출력 읽기
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            // 에러 출력 읽기
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            if (errorOutput.length() > 0) {
                log.warn("파이썬 스크립트 오류 출력: {}", errorOutput);
            }

            // 결과 저장
            int exitCode = process.waitFor();
            log.debug("파이썬 프로세스 종료 코드: {}", exitCode);

            if (exitCode == 0 && output.length() > 0) {
                String jsonData = output.toString().trim();
                log.debug("파싱할 JSON 데이터(일부): {}...",
                        jsonData.substring(0, Math.min(100, jsonData.length())));
                saveNewsData(jsonData);
            } else {
                log.error("파이썬 스크립트에서 유효한 출력이 없거나 스크립트 실행 실패");
            }

            // 임시 파일 삭제
            Files.deleteIfExists(tempFile);
            log.debug("임시 파일 삭제됨");

        } catch (Exception e) {
            log.error("파이썬 스크립트 실행 중 오류 발생", e);
        }
        log.info("뉴스 크롤링 스케줄러 실행 완료");
    }

    @Transactional
    public void saveNewsData(String jsonData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            List<News> newsItems = objectMapper.readValue(jsonData,
                    new TypeReference<List<News>>() {});

            log.info("뉴스 항목 {}개 파싱 성공", newsItems.size());

            // 기존 데이터 모두 삭제
            newsRepository.deleteAll();
            log.info("기존 뉴스 데이터 모두 삭제 완료");

            // 새 데이터 저장
            List<News> savedItems = newsRepository.saveAll(newsItems);
            log.info("뉴스 항목 {}개 저장 성공", savedItems.size());

        } catch (Exception e) {
            log.error("뉴스 데이터 파싱 또는 저장 중 오류 발생", e);
            throw new RuntimeException("뉴스 데이터 저장 실패", e);
        }
    }

    public List<NewsResponse> read() {
        return newsRepository.findAll()
                .stream()
                .map(news -> new NewsResponse(
                        news.getId(),
                        news.getTitle(),
                        news.getLink(),
                        news.getImageUrl()
                ))
                .toList();

    }
}