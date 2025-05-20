package EtfRecommendService.article;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final PythonScriptService pythonScriptService;
    private final ArticleDataReader articleDataReader;

    @PostConstruct
    public void init() {
        log.info("서버 시작 시 뉴스 스크립트 실행");
        executePythonScript(); // 서버 시작 시 최초 1회 실행
    }

    @Scheduled(cron = "0 0 6 * * ?") // 매일 오전 6시에 실행
    @Transactional
    public void executePythonScript() {
        log.info("뉴스 크롤링 스케줄러 실행 시작");
        pythonScriptService.refreshNewsArticles("herald_finance_news_scraper.py");
        List<Article> articles = articleDataReader.readArticles("finance_news.json");

        articleRepository.deleteAll();
        log.info("기존 뉴스 데이터 모두 삭제 완료");

        List<Article> savedArticles = articleRepository.saveAll(articles);
        log.info("뉴스 항목 {}개 저장 성공", savedArticles.size());

        log.info("뉴스 크롤링 스케줄러 실행 완료");
    }

    public List<ArticleResponse> findAll() {
        return articleRepository.findAll()
                .stream()
                .map(article -> new ArticleResponse(
                        article.getId(),
                        article.getTitle(),
                        article.getSourceUrl(),
                        article.getThumbnailUrl(),
                        article.getPublishedAt()
                ))
                .toList();

    }
}