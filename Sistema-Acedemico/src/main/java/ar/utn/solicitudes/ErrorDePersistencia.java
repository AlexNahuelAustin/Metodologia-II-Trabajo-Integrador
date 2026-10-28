package ar.utn.solicitudes;

public class ErrorDePersistencia extends RuntimeException {
    public ErrorDePersistencia(String mensaje) {
        super(mensaje);
    }

    public ErrorDePersistencia(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
