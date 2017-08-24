/* 
 * Copyright (C) 2017 Martijn Courteaux.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.poc.dummydatagenerator;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class LineGenerator {

    public String distanceFormula(String a_lon, String a_lat, String b_lon, String b_lat) {
        String lon_diff = a_lon + " - " + b_lon;
        String lat_diff = a_lat + " - " + b_lat;
        String lat_sum = a_lat + " + " + b_lat;
        return (Math.PI / 180.0) + " * SQRT("
                + "POW((" + lon_diff + ") * COS(" + (Math.PI / 180.0) + " * (" + lat_sum + ") / 2), 2) + "
                + "POW(" + lat_diff + ", 2)"
                + ") * 6371";
    }

    public String distanceFormula(String a_prefix, String b_prefix) {
        return distanceFormula(a_prefix + "_lon", a_prefix + "_lat", b_prefix + "_lon", b_prefix + "_lat");
    }

    public void generate(Connection conn, int numLines) throws SQLException {

        String generateQuery
                = "SELECT "
                + "lfrom.location_id, "
                + "lto.location_id, "
                + distanceFormula("lfrom.location", "lto.location") + " AS distance, "
                + "CONCAT('LINE_', lfrom.location_id, '_', lto.location_id) AS code, "
                + "CASE WHEN random() < 0.1 THEN 'ferry' ELSE 'road' END AS modality "
                + "FROM locations AS lfrom "
                + "INNER JOIN locations AS lto "
                + "ON lfrom.location_id != lto.location_id";
        String sortQuery
                = "SELECT * FROM (" + generateQuery + ") AS temp1 WHERE distance < 200 ORDER BY distance ASC LIMIT " + numLines;
        String query = "INSERT INTO transport_lines (line_from, line_to, line_distance, line_code, line_modality) "
                + "SELECT * FROM (" + sortQuery + ") AS temp";

        System.out.println(sortQuery);

        try (Statement stat = conn.createStatement()) {
            int affected = stat.executeUpdate(query);
            System.out.println("Inserted " + affected + " lines with rough distance between locations.");
        }

    }
}
