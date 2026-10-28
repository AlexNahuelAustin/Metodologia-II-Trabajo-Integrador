package ar.utn.solicitudes.repository;

import ar.utn.solicitudes.ErrorDePersistencia;
import ar.utn.solicitudes.models.Responsable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RepositorioDeResponsablesH2 implements RepositorioDeResponsables{
    private ConexionH2 conexion;

    public RepositorioDeResponsablesH2(ConexionH2 conexion) {
        this.conexion = conexion;
    }

    @Override
    public void registrar(Responsable responsable) {
        try (Connection con = conexion.abrir();
             PreparedStatement ps = con.prepareStatement(
                     "MERGE INTO responsables VALUES (?, ?, ?, ?)")) {

            ps.setString(1, responsable.id);
            ps.setString(2, responsable.nombre);
            ps.setString(3, responsable.area);
            ps.setInt(4, responsable.cantidadAsignada);

            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new ErrorDePersistencia("No se pudo registrar el responsable", ex);
        }
    }

    @Override
    public Responsable obtenerPorId(String id) {
        try (Connection con = conexion.abrir();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM responsables WHERE id = ?")) {

            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Responsable r = new Responsable();
                r.id = rs.getString("id");
                r.nombre = rs.getString("nombre");
                r.area = rs.getString("area");
                r.cantidadAsignada = rs.getInt("cantidad_asignada");
                return r;
            }
        } catch (SQLException ex) {
            throw new ErrorDePersistencia("No se pudo obtener el responsable", ex);
        }
        return null;
    }

    @Override
    public boolean existe(String id) {
        return obtenerPorId(id) != null;
    }

    @Override
    public void incrementarAsignadas(String id) {
        try (Connection con = conexion.abrir();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE responsables SET cantidad_asignada = cantidad_asignada + 1 WHERE id = ?")) {

            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new ErrorDePersistencia("No se pudo incrementar las asignadas", ex);
        }
    }
}
