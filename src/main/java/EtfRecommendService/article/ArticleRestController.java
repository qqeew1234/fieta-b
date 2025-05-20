package EtfRecommendService.article;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ArticleRestController {

    private final ArticleService articleService;

    @GetMapping("/articles")
    public List<ArticleResponse> findAll() {
        return articleService.findAll();
    }
}
