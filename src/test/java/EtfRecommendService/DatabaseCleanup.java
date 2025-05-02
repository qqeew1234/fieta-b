package EtfRecommendService; // 실제 패키지 경로를 확인하세요.

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table; // @Table 어노테이션을 읽기 위해 import
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap; // Map 사용을 위해 import
import java.util.Map; // Map 사용을 위해 import


@Service
public class DatabaseCleanup implements InitializingBean {
    @PersistenceContext
    private EntityManager entityManager;

    // 엔티티 이름을 키, 실제 데이터베이스 테이블 이름을 값으로 저장하는 Map
    private Map<String, String> entityToActualTableNames;

    @Override
    public void afterPropertiesSet() {
        entityToActualTableNames = new HashMap<>();

        // 엔티티 메타모델을 통해 실제 테이블 이름을 얻어옵니다.
        // @Table 어노테이션이 있으면 해당 이름을 사용하고, 없으면 엔티티 이름에서 유추합니다.
        entityManager.getMetamodel().getEntities().stream()
                .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
                .forEach(entityType -> {
                    Class<?> javaType = entityType.getJavaType();
                    String entityName = entityType.getName(); // 엔티티 클래스 이름 (PascalCase)

                    // @Table 어노테이션의 name 속성을 읽어옵니다.
                    Table tableAnnotation = javaType.getAnnotation(Table.class);
                    String actualTableName;

                    if (tableAnnotation != null && !tableAnnotation.name().isEmpty()) {
                        // @Table(name="")이 지정되어 있으면 그 이름을 사용합니다.
                        actualTableName = tableAnnotation.name();
                    } else {
                        // @Table(name="")이 없으면 Spring Data JPA 기본 규칙 (CamelCase -> snake_case)에 따라 유추합니다.
                        // 예: User -> user, CommentLike -> comment_like
                        actualTableName = entityName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
                        // 기본 유추 규칙이 CommentLike -> comment_like 인데 실제 테이블이 comment_likes 인 경우 등
                        // 여기서 추가적인 보정 로직이 필요할 수 있으나, @Table name을 사용하는 것이 가장 정확합니다.
                        // 만약 @Table name이 실제 테이블 이름과 다르면, @Table name을 수정하거나 여기에 매핑을 추가해야 합니다.
                        // 예를 들어, 엔티티 이름이 CommentLike인데 @Table name이 없고 실제 테이블 이름이 comment_likes 라면:
                        // if ("CommentLike".equals(entityName)) { actualTableName = "comment_likes"; }
                        // else { actualTableName = derived from entityName }
                        // 하지만 @Table name을 정확히 설정하는 것을 강력히 권장합니다.
                    }
                    entityToActualTableNames.put(entityName, actualTableName);
                });

        // 실제 테이블 이름들이 올바르게 매핑되었는지 확인 (디버깅용)
        // System.out.println("Mapped Table Names: " + entityToActualTableNames);
    }

    @Transactional
    public void execute() {
        entityManager.flush();
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();

        for (String actualTableName : entityToActualTableNames.values()) {
            String idColumnName = "ID";

            // ① 쌍따옴표 제거
            entityManager.createNativeQuery(
                    "TRUNCATE TABLE " + actualTableName
            ).executeUpdate();

            // ② 시퀀스 리셋도 unquoted
            entityManager.createNativeQuery(
                    "ALTER TABLE " + actualTableName +
                            " ALTER COLUMN " + idColumnName +
                            " RESTART WITH 1"
            ).executeUpdate();
        }

        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }

}