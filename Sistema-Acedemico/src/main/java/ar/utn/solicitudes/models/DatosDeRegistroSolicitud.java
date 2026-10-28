package ar.utn.solicitudes.models;

// Datos necesarios para registrar una solicitud académica.
public record DatosDeRegistroSolicitud(
        String legajo,
        String nombre,
        String apellido,
        String email,
        String telefono,
        String carrera,
        String tipo,
        String observaciones
) {
}
