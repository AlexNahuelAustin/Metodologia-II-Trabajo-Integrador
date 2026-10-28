package ar.utn.solicitudes.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionH2 {
    private static final String URL = "jdbc:h2:./datos/academico";
    private static final String USUARIO = "sa";
    private static final String CLAVE = "";

    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
