package personnel;

/**
 * Exception levée lorsqu'une incohérence est détectée entre les dates
 * d'arrivée et de départ d'un employé.
 */

public class DateIncoherenteException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DateIncoherenteException(String message) {
        super(message);
    }
}
