/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.martijncourteaux.supplychainplanner.poc;

import com.martijncourteaux.supplychainplanner.generic.AbstractContext;
import com.martijncourteaux.supplychainplanner.persistence.FieldValueMapper;
import com.martijncourteaux.supplychainplanner.poc.model.PoCLocation;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author martijn
 */
public class PoCContext extends AbstractContext {

    private final Map<Long, PoCLocation> locationMap = new HashMap<>();

    @Override
    public void prepareContext() throws Exception {
        connectDatabase();
        loadLocations();
    }

    @Override
    public void destroyContext() throws Exception {
        disconnectDatabase();
    }

    @Override
    public void connectDatabase() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            String db_uri = "jdbc:postgresql://localhost:5432/poc";
            String db_user = "postgres";
            String db_pass = "";
            connection = DriverManager.getConnection(db_uri, db_user, db_pass);
        } catch (ClassNotFoundException ex) {
            System.err.println("Classpath issues: postgres JDBC driver not found.");
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public void loadLocations() throws SQLException {
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM locations;")) {

            FieldValueMapper<PoCLocation> mapper = new FieldValueMapper<>(PoCLocation.class);
            mapper.prepare(rs);

            while (rs.next()) {
                PoCLocation loc = new PoCLocation();
                mapper.extract(rs, loc);
                locationMap.put(loc.id, loc);
            }

        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(PoCContext.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PoCLocation getLocation(long id) {
        return locationMap.get(id);
    }
}
