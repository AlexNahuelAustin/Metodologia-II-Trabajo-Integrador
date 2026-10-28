package ar.utn.solicitudes.models;

public enum EstadoSolicitud {
    PENDIENTE("pendiente"),
    EN_PROCESO("en_proceso"),
    RESUELTA("resuelta"),
    RECHAZADA("rechazada");

    private final String valor;

    EstadoSolicitud(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public static EstadoSolicitud fromValor(String valor) {
        for (EstadoSolicitud e : values()) {
            if (e.valor.equals(valor)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Estado no válido: " + valor);
    }

}