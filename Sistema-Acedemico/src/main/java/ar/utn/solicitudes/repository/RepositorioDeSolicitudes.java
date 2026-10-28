package ar.utn.solicitudes.repository;

import ar.utn.solicitudes.models.Solicitud;

import java.util.List;

public interface RepositorioDeSolicitudes {
    int registrar(Solicitud solicitud);
    boolean actualizar(Solicitud solicitud);
    Solicitud obtenerPorId(int id);
    List<Solicitud> obtenerPorEstado(String estado);
    List<Solicitud> obtenerPorTipo(String tipo);
    List<Solicitud> obtenerPorResponsable(String idResponsable);
    List<Solicitud> obtenerTodas();
}
