package large.file.reading.challenge.app.exception;

/**
 * @author Jarosław Kormański
 */
public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(String message) {
        super(message);
    }
}
