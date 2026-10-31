package cotiinformatica.factories;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionFactory {

    private String host;
    private String user;
    private String pass;

    public Connection createConnection() throws Exception {

        var host = "jdbc:postgresql://localhost:5434/bd-funcionarios";
        var user = "coti";
        var pass = "coti";

        return DriverManager.getConnection(host, user, pass);
    }
}
