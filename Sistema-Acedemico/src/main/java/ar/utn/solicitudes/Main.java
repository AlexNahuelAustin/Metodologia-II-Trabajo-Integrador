package ar.utn.solicitudes;

import ar.utn.solicitudes.models.Responsable;
import ar.utn.solicitudes.models.DatosDeRegistroSolicitud;



public class Main {
    public static void main(String[] args) {
        GestorDeSolicitudes gestor = new GestorDeSolicitudes();
        gestor.inicializar();

        // Refactoring 1: Usa el record
        var datosJuan = new DatosDeRegistroSolicitud(
                "E001",
                "Juan",
                "Perez",
                "juan@mail.com",
                "1234567890",
                "Informatica",
                "certificado",
                "Urgente"
        );
        int id1 = gestor.registrarSolicitud(datosJuan);

        var datosMaría = new DatosDeRegistroSolicitud(
                "E002",
                "María",
                "García",
                "maria@mail.com",
                "0987654321",
                "Sistemas",
                "equivalencia",
                "Normal"
        );
        int id2 = gestor.registrarSolicitud(datosMaría);

        // El constructor recibe id, nombre y área.
        Responsable r1 = new Responsable(
                "R001",
                "Carlos López",
                "Académica"
        );

        // Registrar el responsable en la base de datos.
        gestor.altaResponsable(r1);

        // Asignar responsables
        gestor.asignarResponsable(id1, "R001");

        // Cambiar estado
        gestor.cambiarEstado(id1, "en_proceso");

        // Generar reporte
        gestor.generarReporte();

    }


}