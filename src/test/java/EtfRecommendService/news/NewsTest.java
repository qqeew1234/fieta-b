package EtfRecommendService.news;


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

public class NewsTest {

    @Mock
    private NewsRepository newsRepository;

    @InjectMocks
    private NewsService newsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    public void testRead() {
        // Given
        News news1 = new News();
        ReflectionTestUtils.setField(news1, "title", "Title1");
        ReflectionTestUtils.setField(news1, "link", "Link1");
        ReflectionTestUtils.setField(news1, "imageUrl", "ImageUrl1");

        News news2 = new News();
        ReflectionTestUtils.setField(news2, "title", "Title2");
        ReflectionTestUtils.setField(news2, "link", "Link2");
        ReflectionTestUtils.setField(news2, "imageUrl", "ImageUrl2");

        when(newsRepository.findAll()).thenReturn(Arrays.asList(news1, news2));

        // When
        List<NewsResponse> responses = newsService.read();

        // Then
        assertEquals(2, responses.size());
        assertEquals("Title1", responses.get(0).newsTitle());
        assertEquals("Link1", responses.get(0).newsLink());
        assertEquals("ImageUrl1", responses.get(0).imageUrl());
        assertEquals("Title2", responses.get(1).newsTitle());
        assertEquals("Link2", responses.get(1).newsLink());
        assertEquals("ImageUrl2", responses.get(1).imageUrl());

        verify(newsRepository, times(1)).findAll();
    }
}
