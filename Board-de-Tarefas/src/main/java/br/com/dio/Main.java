package br.com.dio;

import br.com.dio.persistence.migration.MigrationStrategy;

import java.sql.SQLException;

import static br.com.dio.persistence.config.ConnectionConfig.getConnection;


public class Main {
    public static void main(String[] args) throws SQLException {
        try(var connection = getConnection()){
            new MigrationStrategy(connection).executeMigration();
        }
        System.out.println("Hello, World!");
    }
}
