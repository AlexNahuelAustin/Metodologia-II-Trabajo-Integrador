package ar.utn.solicitudes.repository;

import ar.utn.solicitudes.ErrorDePersistencia;
import ar.utn.solicitudes.models.Estudiante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RepositorioDeEstudiantesH2 implements RepositorioDeEstudiantes{
    private ConexionH2 conexion;

    public RepositorioDeEstudiantesH2(ConexionH2 conexion) {
        this.conexion = conexion;
    }

    @Override
    public void registrar(Estudiante estudiante) {
        try (Connection con = conexion.abrir();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO estudiantes VALUES (?, ?, ?, ?, ?, ?)")) {

            ps.setString(1, estudiante.legajo);
            ps.setString(2, estudiante.nombre);
            ps.setString(3, estudiante.apellido);
            ps.setString(4, estudiante.email);
            ps.setString(5, estudiante.telefono);
            ps.setString(6, estudiante.carrera);

            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new ErrorDePersistencia("No se pudo registrar el estudiante", ex);
        }
    }

    @Override
    public Estudiante obtenerPorLegajo(String legajo) {
        try (Connection con = conexion.abrir();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM estudiantes WHERE legajo = ?")) {

            ps.setString(1, legajo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Estudiante e = new Estudiante();
                e.legajo = rs.getString("legajo");
                e.nombre = rs.getString("nombre");
                e.apellido = rs.getString("apellido");
                e.email = rs.getString("email");
                e.telefono = rs.getString("telefono");
                e.carrera = rs.getString("carrera");
                return e;
            }
        } catch (SQLException ex) {
            throw new ErrorDePersistencia("No se pudo obtener el estudiante", ex);
        }
        return null;
    }

    @Override
    public boolean existe(String legajo) {
        return obtenerPorLegajo(legajo) != null;
    }
}
