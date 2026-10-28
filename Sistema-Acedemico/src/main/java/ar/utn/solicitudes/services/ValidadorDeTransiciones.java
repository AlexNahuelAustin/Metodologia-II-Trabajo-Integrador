package ar.utn.solicitudes.services;

import ar.utn.solicitudes.models.EstadoSolicitud;

public class ValidadorDeTransiciones {
    public boolean esTransicionValida(EstadoSolicitud estadoActual, EstadoSolicitud nuevoEstado, boolean tieneResponsable) {
        return switch (estadoActual) {
            case PENDIENTE -> esValidaDesdePendiente(nuevoEstado, tieneResponsable);
            case EN_PROCESO -> esValidaDesdeEnProceso(nuevoEstado);
            case RESUELTA, RECHAZADA -> false;
        };
    }

    private boolean esValidaDesdePendiente(EstadoSolicitud nuevoEstado, boolean tieneResponsable) {
        if (nuevoEstado != EstadoSolicitud.EN_PROCESO && nuevoEstado != EstadoSolicitud.RECHAZADA) {
            return false;
        }
        if (nuevoEstado == EstadoSolicitud.EN_PROCESO && !tieneResponsable) {
            return false;
        }
        return true;
    }

    private boolean esValidaDesdeEnProceso(EstadoSolicitud nuevoEstado) {
        return nuevoEstado == EstadoSolicitud.RESUELTA || nuevoEstado == EstadoSolicitud.RECHAZADA;
    }
}
