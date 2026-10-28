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
        validarDatos(datos);
        TipoSolicitud tipo = validarYObtenerTipo(datos.tipo());
        registrarEstudianteQueNoExista(datos);
        validarQueNoTengaDemasidasSolicitudesAbiertas(datos.legajo());
        return crearYRegistrarSolicitud(datos, tipo);
    }

    private void validarDatos(DatosDeRegistroSolicitud datos) {
        if (datos.legajo() == null || datos.legajo().isEmpty()) {
            throw new IllegalArgumentException("ERROR: falta el legajo");
        }
        if (datos.email() == null || !datos.email().contains("@")) {
            throw new IllegalArgumentException("ERROR: email invalido");
        }
    }

    private TipoSolicitud validarYObtenerTipo(String tipo) {
        try {
            return TipoSolicitud.desdeValor(tipo);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("ERROR: tipo invalido");
        }
    }

    private void registrarEstudianteQueNoExista(DatosDeRegistroSolicitud datos) {
        if (!repositorioEstudiantes.existe(datos.legajo())) {
            Estudiante estudiante = crearEstudiante(datos);
            repositorioEstudiantes.registrar(estudiante);
        }
    }

    private Estudiante crearEstudiante(DatosDeRegistroSolicitud datos) {
        Estudiante estudiante = new Estudiante();
        estudiante.legajo = datos.legajo();
        estudiante.nombre = datos.nombre();
        estudiante.apellido = datos.apellido();
        estudiante.email = datos.email();
        estudiante.telefono = datos.telefono();
        estudiante.carrera = datos.carrera();
        return estudiante;
    }

    private void validarQueNoTengaDemasidasSolicitudesAbiertas(String legajo) {
        List<Solicitud> abiertas = repositorioSolicitudes.obtenerPorEstado(EstadoSolicitud.PENDIENTE.obtenerValor());
        long cantidadAbiertas = abiertas.stream()
                .filter(s -> s.legajoEstudiante.equals(legajo))
                .count();

        if (cantidadAbiertas >= ConstantesSolicitudes.MAX_SOLICITUDES_ABIERTAS_POR_ESTUDIANTE) {
            throw new IllegalArgumentException("ERROR: el estudiante tiene demasiadas solicitudes abiertas");
        }
    }

    private int crearYRegistrarSolicitud(DatosDeRegistroSolicitud datos, TipoSolicitud tipo) {
        Solicitud solicitud = crearSolicitud(datos, tipo);
        return repositorioSolicitudes.registrar(solicitud);
    }

    private Solicitud crearSolicitud(DatosDeRegistroSolicitud datos, TipoSolicitud tipo) {
        Solicitud solicitud = new Solicitud();
        solicitud.legajoEstudiante = datos.legajo();
        solicitud.tipo = tipo.obtenerValor();
        solicitud.estado = EstadoSolicitud.PENDIENTE.obtenerValor();
        solicitud.prioridad = tipo.obtenerPrioridad();
        solicitud.fechaAlta = LocalDate.now();
        solicitud.observaciones = datos.observaciones();
        return solicitud;
    }

    public boolean cambiarEstado(int id, String nuevoEstadoStr) {
        EstadoSolicitud nuevoEstado = validarYObtenerEstado(nuevoEstadoStr);
        Solicitud solicitud = obtenerSolicitudOLanzarError(id);
        validarTransicion(solicitud, nuevoEstado);
        actualizarSolicitudConNuevoEstado(solicitud, nuevoEstado);
        return repositorioSolicitudes.actualizar(solicitud);
    }

    private EstadoSolicitud validarYObtenerEstado(String nuevoEstadoStr) {
        try {
            return EstadoSolicitud.desdeValor(nuevoEstadoStr);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("ERROR: estado no válido");
        }
    }

    private Solicitud obtenerSolicitudOLanzarError(int id) {
        Solicitud solicitud = repositorioSolicitudes.obtenerPorId(id);
        if (solicitud == null) {
            throw new IllegalArgumentException("ERROR: no existe la solicitud");
        }
        return solicitud;
    }

    private void validarTransicion(Solicitud solicitud, EstadoSolicitud nuevoEstado) {
        EstadoSolicitud estadoActual = EstadoSolicitud.desdeValor(solicitud.estado);

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
    }

    private void actualizarSolicitudConNuevoEstado(Solicitud solicitud, EstadoSolicitud nuevoEstado) {
        solicitud.estado = nuevoEstado.obtenerValor();
        if (nuevoEstado == EstadoSolicitud.RESUELTA || nuevoEstado == EstadoSolicitud.RECHAZADA) {
            solicitud.fechaCierre = LocalDate.now();
        }
    }

    public boolean asignarResponsable(int id, String idResponsable) {
        Solicitud solicitud = obtenerSolicitudOLanzarError(id);
        Responsable responsable = obtenerResponsableOLanzarError(idResponsable);
        validarQueResponsableNoEsteSobrecargado(responsable);
        solicitud.idResponsable = idResponsable;
        repositorioResponsables.incrementarAsignadas(idResponsable);
        return repositorioSolicitudes.actualizar(solicitud);
    }

    private Responsable obtenerResponsableOLanzarError(String idResponsable) {
        Responsable responsable = repositorioResponsables.obtenerPorId(idResponsable);
        if (responsable == null) {
            throw new IllegalArgumentException("ERROR: no existe el responsable");
        }
        return responsable;
    }

    private void validarQueResponsableNoEsteSobrecargado(Responsable responsable) {
        if (responsable.cantidadAsignada >= ConstantesSolicitudes.MAX_SOLICITUDES_POR_RESPONSABLE) {
            throw new IllegalArgumentException("ERROR: el responsable esta sobrecargado");
        }
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
