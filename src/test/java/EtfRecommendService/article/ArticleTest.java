package EtfRecommendService.article;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ArticleTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleService articleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testRead() {
        // Given
        Article article1 = new Article();
        ReflectionTestUtils.setField(article1, "title", "Title1");
        ReflectionTestUtils.setField(article1, "sourceUrl", "Link1");
        ReflectionTestUtils.setField(article1, "thumbnailUrl", "ImageUrl1");

        Article article2 = new Article();
        ReflectionTestUtils.setField(article2, "title", "Title2");
        ReflectionTestUtils.setField(article2, "sourceUrl", "Link2");
        ReflectionTestUtils.setField(article2, "thumbnailUrl", "ImageUrl2");

        when(articleRepository.findAll()).thenReturn(Arrays.asList(article1, article2));

        // When
        List<ArticleResponse> responses = articleService.findAll();

        // Then
        assertEquals(2, responses.size());
        assertEquals("Title1", responses.get(0).title());
        assertEquals("Link1", responses.get(0).sourceUrl());
        assertEquals("ImageUrl1", responses.get(0).thumbnailUrl());
        assertEquals("Title2", responses.get(1).title());
        assertEquals("Link2", responses.get(1).sourceUrl());
        assertEquals("ImageUrl2", responses.get(1).thumbnailUrl());

        verify(articleRepository, times(1)).findAll();
    }
}
