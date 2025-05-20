package EtfRecommendService.article;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Service
public class ArticleDataReader {
    private static final Logger logger = LoggerFactory.getLogger(ArticleDataReader.class);
    private final ObjectMapper objectMapper;

    public ArticleDataReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper.copy();
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    public List<Article> readArticles(String fileName) {
        try {
            File file = getJsonFile(fileName);
            return objectMapper.readValue(file, new TypeReference<>() {
            });
        } catch (IOException e) {
            logger.error("Failed to read JSON file: {}", fileName, e);
            return Collections.emptyList();
        }
    }

    /**
     * 프로젝트 루트의 scripts/ 디렉터리에서 JSON 파일 가져오기
     */
    private File getJsonFile(String fileName) {
        // 프로젝트 루트 디렉터리 경로 가져오기
        String rootDir = System.getProperty("user.dir");
        String filePath = Paths.get(rootDir, "scripts", fileName).toString();

        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new RuntimeException("File not found: " + filePath);
        }
        return file;
    }
}