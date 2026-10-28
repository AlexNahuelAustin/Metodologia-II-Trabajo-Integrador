package ar.utn.solicitudes.models;

public enum TipoSolicitud {
    CERTIFICADO("certificado", 3),
    EQUIVALENCIA("equivalencia", 2),
    REINCORPORACION("reincorporacion", 1);

    private final String valor;
    private final int prioridad;

    TipoSolicitud(String valor, int prioridad) {
        this.valor = valor;
        this.prioridad = prioridad;
    }

    public String obtenerValor() {
        return valor;
    }

    public int obtenerPrioridad() {
        return prioridad;
    }

    public static TipoSolicitud desdeValor(String valor) {
        for (TipoSolicitud t : values()) {
            if (t.valor.equals(valor)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo no válido: " + valor);
    }
}
