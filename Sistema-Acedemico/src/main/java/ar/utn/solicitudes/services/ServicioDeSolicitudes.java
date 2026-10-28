package ar.utn.solicitudes.services;

import ar.utn.solicitudes.models.*;
import ar.utn.solicitudes.repository.RepositorioDeEstudiantes;
import ar.utn.solicitudes.repository.RepositorioDeResponsables;
import ar.utn.solicitudes.repository.RepositorioDeSolicitudes;

import java.time.LocalDate;
import java.util.List;

public class ServicioDeSolicitudes {
    private RepositorioDeSolicitudes repositorioSolicitudes;
    private RepositorioDeEstudiantes repositorioEstudiantes;
    private RepositorioDeResponsables repositorioResponsables;

    public ServicioDeSolicitudes(
            RepositorioDeSolicitudes repositorioSolicitudes,
            RepositorioDeEstudiantes repositorioEstudiantes,
            RepositorioDeResponsables repositorioResponsables) {
        this.repositorioSolicitudes = repositorioSolicitudes;
        this.repositorioEstudiantes = repositorioEstudiantes;
        this.repositorioResponsables = repositorioResponsables;
    }

    public int registrarSolicitud(DatosDeRegistroSolicitud datos) {
        // validaciones
        if (datos.legajo() == null || datos.legajo().isEmpty()) {
            throw new IllegalArgumentException("ERROR: falta el legajo");
        }
        if (datos.email() == null || !datos.email().contains("@")) {
            throw new IllegalArgumentException("ERROR: email invalido");
        }

        // Valida tipo usando el enum
        TipoSolicitud tipo;
        try {
            tipo = TipoSolicitud.desdeValor(datos.tipo());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("ERROR: tipo invalido");
        }

        // Registra estudiante si no existe
        if (!repositorioEstudiantes.existe(datos.legajo())) {
            Estudiante estudiante = new Estudiante();
            estudiante.legajo = datos.legajo();
            estudiante.nombre = datos.nombre();
            estudiante.apellido = datos.apellido();
            estudiante.email = datos.email();
            estudiante.telefono = datos.telefono();
            estudiante.carrera = datos.carrera();
            repositorioEstudiantes.registrar(estudiante);
        }

        // Valida que no tenga demasiadas solicitudes abiertas
        List<Solicitud> abiertas = repositorioSolicitudes.obtenerPorEstado(EstadoSolicitud.PENDIENTE.obtenerValor());
        long cantidadAbiertas = abiertas.stream()
                .filter(s -> s.legajoEstudiante.equals(datos.legajo()))
                .count();

        if (cantidadAbiertas >= ConstantesSolicitudes.MAX_SOLICITUDES_ABIERTAS_POR_ESTUDIANTE) {
            throw new IllegalArgumentException("ERROR: el estudiante tiene demasiadas solicitudes abiertas");
        }

        // Crea y registra la solicitud
        Solicitud solicitud = new Solicitud();
        solicitud.legajoEstudiante = datos.legajo();
        solicitud.tipo = tipo.obtenerValor();
        solicitud.estado = EstadoSolicitud.PENDIENTE.obtenerValor();
        solicitud.prioridad = tipo.obtenerPrioridad();
        solicitud.fechaAlta = LocalDate.now();
        solicitud.observaciones = datos.observaciones();

        return repositorioSolicitudes.registrar(solicitud);
    }

    public boolean cambiarEstado(int id, String nuevoEstadoStr) {
        EstadoSolicitud nuevoEstado;
        try {
            nuevoEstado = EstadoSolicitud.desdeValor(nuevoEstadoStr);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("ERROR: estado no válido");
        }

        Solicitud solicitud = repositorioSolicitudes.obtenerPorId(id);
        if (solicitud == null) {
            throw new IllegalArgumentException("ERROR: no existe la solicitud");
        }

        EstadoSolicitud estadoActual = EstadoSolicitud.desdeValor(solicitud.estado);

        // Valida transiciones
        if (estadoActual == EstadoSolicitud.PENDIENTE) {
            if (nuevoEstado != EstadoSolicitud.EN_PROCESO && nuevoEstado != EstadoSolicitud.RECHAZADA) {
                throw new IllegalArgumentException("ERROR: transicion invalida");
            }
            if (nuevoEstado == EstadoSolicitud.EN_PROCESO && solicitud.idResponsable == null) {
                throw new IllegalArgumentException("ERROR: no se puede procesar sin responsable");
            }
        } else if (estadoActual == EstadoSolicitud.EN_PROCESO) {
            if (nuevoEstado != EstadoSolicitud.RESUELTA && nuevoEstado != EstadoSolicitud.RECHAZADA) {
                throw new IllegalArgumentException("ERROR: transicion invalida");
            }
        } else {
            throw new IllegalArgumentException("ERROR: la solicitud ya esta cerrada");
        }

        solicitud.estado = nuevoEstado.obtenerValor();
        if (nuevoEstado == EstadoSolicitud.RESUELTA || nuevoEstado == EstadoSolicitud.RECHAZADA) {
            solicitud.fechaCierre = LocalDate.now();
        }

        return repositorioSolicitudes.actualizar(solicitud);
    }

    public boolean asignarResponsable(int id, String idResponsable) {
        Solicitud solicitud = repositorioSolicitudes.obtenerPorId(id);
        if (solicitud == null) {
            throw new IllegalArgumentException("ERROR: no existe la solicitud");
        }

        Responsable responsable = repositorioResponsables.obtenerPorId(idResponsable);
        if (responsable == null) {
            throw new IllegalArgumentException("ERROR: no existe el responsable");
        }

        if (responsable.cantidadAsignada >= ConstantesSolicitudes.MAX_SOLICITUDES_POR_RESPONSABLE) {
            throw new IllegalArgumentException("ERROR: el responsable esta sobrecargado");
        }

        solicitud.idResponsable = idResponsable;
        repositorioResponsables.incrementarAsignadas(idResponsable);

        return repositorioSolicitudes.actualizar(solicitud);
    }

    public List<Solicitud> obtenerSolicitudesPorEstado(String estado) {
        return repositorioSolicitudes.obtenerPorEstado(estado);
    }

    public List<Solicitud> obtenerSolicitudesPorTipo(String tipo) {
        return repositorioSolicitudes.obtenerPorTipo(tipo);
    }

    public List<Solicitud> obtenerSolicitudesPorResponsable(String idResponsable) {
        return repositorioSolicitudes.obtenerPorResponsable(idResponsable);
    }
}
