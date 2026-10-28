package ar.utn.solicitudes.repository;

import ar.utn.solicitudes.models.Estudiante;

public interface RepositorioDeEstudiantes {
    void registrar(Estudiante estudiante);
    Estudiante obtenerPorLegajo(String legajo);
    boolean existe(String legajo);
}
