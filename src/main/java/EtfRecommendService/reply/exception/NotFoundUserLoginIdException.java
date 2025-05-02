package EtfRecommendService.reply.exception;

public class NotFoundUserLoginIdException extends RuntimeException {
    public NotFoundUserLoginIdException(String message) {
        super(message);
    }
}
