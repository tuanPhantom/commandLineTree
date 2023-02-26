package common;

/**
 * @Overview A class represents an exception caused by invalid arguments when creating a new object
 * @author Phan Quang Tuan
 * @version since 1.0
 */
public class NotPossibleException extends Exception{
    public NotPossibleException() {
        super();
    }

    public NotPossibleException(String message) {
        super(message);
    }
}
