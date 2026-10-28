package ar.utn.solicitudes.repository;

import ar.utn.solicitudes.ErrorDePersistencia;
import ar.utn.solicitudes.models.Solicitud;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDeSolicitudesH2 implements RepositorioDeSolicitudes{
    private ConexionH2 conexion;

    public RepositorioDeSolicitudesH2(ConexionH2 conexion) {
        this.conexion = conexion;
    }

    @Override
    public int registrar(Solicitud solicitud) {
        try (Connection con = conexion.abrir();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO solicitudes (id, legajo_estudiante, tipo, estado, prioridad, id_responsable, fecha_alta, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, obtenerProximoId());
            ps.setString(2, solicitud.legajoEstudiante);
            ps.setString(3, solicitud.tipo);
            ps.setString(4, solicitud.estado);
            ps.setInt(5, solicitud.prioridad);
            ps.setString(6, solicitud.idResponsable);
            ps.setDate(7, Date.valueOf(solicitud.fechaAlta));
            ps.setString(8, solicitud.observaciones);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new ErrorDePersistencia("No se pudo registrar la solicitud", ex);
        }
        return -1;
    }

    @Override
    public boolean actualizar(Solicitud solicitud) {
        try (Connection con = conexion.abrir();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE solicitudes SET estado = ?, id_responsable = ?, fecha_cierre = ? WHERE id = ?")) {

            ps.setString(1, solicitud.estado);
            ps.setString(2, solicitud.idResponsable);
            if (solicitud.fechaCierre != null) {
                ps.setDate(3, Date.valueOf(solicitud.fechaCierre));
            } else {
                ps.setNull(3, Types.DATE);
            }
            ps.setInt(4, solicitud.id);

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new ErrorDePersistencia("No se pudo actualizar la solicitud", ex);
        }
    }

    @Override
    public Solicitud obtenerPorId(int id) {
        try (Connection con = conexion.abrir();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM solicitudes WHERE id = ?")) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearSolicitud(rs);
            }
        } catch (SQLException ex) {
            throw new ErrorDePersistencia("No se pudo obtener la solicitud", ex);
        }
        return null;
    }

    @Override
    public List<Solicitud> obtenerPorEstado(String estado) {
        return buscarPor("estado", estado);
    }

    @Override
    public List<Solicitud> obtenerPorTipo(String tipo) {
        return buscarPor("tipo", tipo);
    }

    @Override
    public List<Solicitud> obtenerPorResponsable(String idResponsable) {
        return buscarPor("id_responsable", idResponsable);
    }

    @Override
    public List<Solicitud> obtenerTodas() {
        List<Solicitud> solicitudes = new ArrayList<>();
        try (Connection con = conexion.abrir();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM solicitudes")) {

            while (rs.next()) {
                solicitudes.add(mapearSolicitud(rs));
            }
        } catch (SQLException ex) {
            throw new ErrorDePersistencia("No se pudo obtener las solicitudes", ex);
        }
        return solicitudes;
    }

    private List<Solicitud> buscarPor(String columna, String valor) {
        List<Solicitud> solicitudes = new ArrayList<>();
        try (Connection con = conexion.abrir();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM solicitudes WHERE " + columna + " = ?")) {

            ps.setString(1, valor);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                solicitudes.add(mapearSolicitud(rs));
            }
        } catch (SQLException ex) {
            throw new ErrorDePersistencia("No se pudo buscar solicitudes", ex);
        }
        return solicitudes;
    }

    private Solicitud mapearSolicitud(ResultSet rs) throws SQLException {
        Solicitud s = new Solicitud();
        s.id = rs.getInt("id");
        s.legajoEstudiante = rs.getString("legajo_estudiante");
        s.tipo = rs.getString("tipo");
        s.estado = rs.getString("estado");
        s.prioridad = rs.getInt("prioridad");
        s.idResponsable = rs.getString("id_responsable");
        s.fechaAlta = rs.getDate("fecha_alta").toLocalDate();
        if (rs.getDate("fecha_cierre") != null) {
            s.fechaCierre = rs.getDate("fecha_cierre").toLocalDate();
        }
        s.observaciones = rs.getString("observaciones");
        return s;
    }

    private int obtenerProximoId() {
        try (Connection con = conexion.abrir();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT MAX(id) FROM solicitudes")) {

            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        } catch (SQLException ex) {
            throw new ErrorDePersistencia("No se pudo obtener el próximo ID", ex);
        }
        return 1;
    }
}
