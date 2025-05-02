package EtfRecommendService;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String fileName = UUID.randomUUID().toString();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());
        metadata.setHeader("Content-Disposition", "inline");

        try (InputStream inputStream = multipartFile.getInputStream()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    fileName,
                    inputStream,
                    metadata
            );

            amazonS3.putObject(putObjectRequest);
        }

        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            log.warn("삭제할 파일 URL이 비어있습니다.");
            return;
        }

        try {
            // URL 출력하여 디버깅
            log.info("삭제 요청된 파일 URL: {}", fileUrl);

            // 파일 키 추출 - 수정된 방식
            String fileName = extractFileKeyFromUrl(fileUrl);
            log.info("추출된 파일 키: {}", fileName);

            // 파일 존재 여부 확인
            boolean exists = amazonS3.doesObjectExist(bucketName, fileName);
            if (!exists) {
                log.warn("S3에 파일이 존재하지 않습니다: {}", fileName);
                return;
            }

            // 삭제 실행
            amazonS3.deleteObject(bucketName, fileName);
            log.info("S3 파일 삭제 성공: {}", fileName);

        } catch (AmazonServiceException e) {
            log.error("S3 서비스 오류: 상태코드={}, 에러코드={}, 메시지={}",
                    e.getStatusCode(), e.getErrorCode(), e.getMessage(), e);
            throw new RuntimeException("S3 파일 삭제 중 오류 발생", e);
        } catch (Exception e) {
            log.error("S3 파일 삭제 중 예외 발생: {}", e.getMessage(), e);
            throw new RuntimeException("S3 파일 삭제 중 오류 발생", e);
        }
    }

    private String extractFileKeyFromUrl(String fileUrl) {
        try {
            log.info("URL 파싱 시작: {}", fileUrl);

            // 일반적인 S3 URL 형식: https://{bucket}.s3.{region}.amazonaws.com/{key}
            // 또는 https://s3.{region}.amazonaws.com/{bucket}/{key}

            URL url = new URL(fileUrl);
            String path = url.getPath();
            String host = url.getHost();

            log.info("URL 분석 - 호스트: {}, 경로: {}", host, path);

            // 경로 스타일 URL이면 버킷명 이후의 경로만 추출
            if (host.contains("s3.") && host.contains("amazonaws.com") && path.startsWith("/" + bucketName + "/")) {
                return path.substring(bucketName.length() + 2); // 버킷명과 슬래시 두 개 제거
            }
            // 가상 호스팅 스타일 URL이면 첫 슬래시 이후 모두 파일키
            else if (host.startsWith(bucketName + ".")) {
                return path.startsWith("/") ? path.substring(1) : path;
            }
            // URL에서 직접 파일명만 추출하는 대안 방법
            else {
                // 마지막 슬래시 이후의 문자열을 키로 간주 (타임스탬프_원본파일명)
                int lastSlashIndex = fileUrl.lastIndexOf('/');
                if (lastSlashIndex != -1 && lastSlashIndex < fileUrl.length() - 1) {
                    return fileUrl.substring(lastSlashIndex + 1);
                }
            }

            log.warn("URL에서 키를 추출할 수 없습니다. 원본 경로를 사용합니다: {}", path);
            return path.startsWith("/") ? path.substring(1) : path;

        } catch (Exception e) {
            log.error("URL 파싱 중 오류: {}", e.getMessage(), e);
            throw new IllegalArgumentException("잘못된 S3 URL 형식입니다: " + fileUrl, e);
        }
    }


}