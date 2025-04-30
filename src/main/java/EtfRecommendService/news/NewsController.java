package EtfRecommendService.news;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/news")
    public ResponseEntity<List<NewsResponse>> read(){
        List<NewsResponse> responses = newsService.read();
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}
