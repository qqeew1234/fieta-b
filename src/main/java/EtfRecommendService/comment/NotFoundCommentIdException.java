package EtfRecommendService.comment;

public class NotFoundCommentIdException extends RuntimeException {
    public NotFoundCommentIdException(String message) {
        super(message);
    }
}
