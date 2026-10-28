package ar.utn.solicitudes.repository;

import ar.utn.solicitudes.models.Responsable;

public interface RepositorioDeResponsables {
    void registrar(Responsable responsable);
    Responsable obtenerPorId(String id);
    boolean existe(String id);
    void incrementarAsignadas(String id);
}
