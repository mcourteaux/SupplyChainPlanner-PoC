/* 
 * Copyright (C) 2017 Martijn Courteaux.
 *
 * For license details, see LICENSE.txt and README.txt.
 * This project is licensed under Creative Commons NC-NC-ND.
 */
package com.martijncourteaux.supplychainplanner.poc.dummydatagenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

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

    public void generate_old(Connection conn, int numLines, int numLocations) throws SQLException {
        String query
                = "INSERT INTO transport_lines (line_from, line_to, line_distance, line_code, line_modality) VALUES(?, ?, ?, ?, ?);";
        try (PreparedStatement stat = conn.prepareStatement(query)) {
            Random random = new Random();

            for (int i = 0; i < numLines; ++i) {
                int loc_from = random.nextInt(numLocations);
                int loc_to = (loc_from + random.nextInt(20) + numLocations - 10) % numLocations;
                if (loc_to == loc_from) {
                    loc_to++;
                }
                stat.setInt(1, loc_from + 1);
                stat.setInt(2, loc_to + 1);
                stat.setDouble(3, random.nextDouble() * 1000);
                stat.setString(4, String.format("LINE_%05d", i + 1));
                String modality;
                switch (random.nextInt(4)) {
                    case 0:
                    case 1:
                    case 2:
                        modality = "road";
                        break;

                    case 3:
                        modality = "ferry";
                        break;
                    default:
                        modality = null;
                }
                stat.setString(5, modality);

                stat.addBatch();
                if (i % 100 == 0) {
                    stat.executeBatch();
                    System.out.println("Inserted lines " + i);
                    stat.clearBatch();
                }
            }
            stat.executeBatch();
            stat.clearBatch();
        }
        System.out.println("Done");

        /*
         * We will calculate approximate distances for all inserted lines.
         */
        query = "UPDATE transport_lines "
                + "SET line_distance=subquery.calced_dist "
                + "FROM ("
                + "   SELECT "
                + "     0.01745329252 * SQRT("
                + "        POW((lfrom.location_lon - lto.location_lon) * COS((lfrom.location_lat + lto.location_lat) / 2), 2) + "
                + "        POW(lfrom.location_lat - lto.location_lat, 2)"
                + "     ) * 6371          AS calced_dist, "
                + "     lfrom.location_id AS from_id, "
                + "     lto.location_id   AS to_id "
                + "   FROM       locations AS lfrom "
                + "   CROSS JOIN locations AS lto) AS subquery "
                + "WHERE transport_lines.line_from = subquery.from_id "
                + " AND  transport_lines.line_to   = subquery.to_id";
        try (Statement stat = conn.createStatement()) {
            int affected = stat.executeUpdate(query);
            System.out.println("Calculated rough distance between locations on " + affected + " lines.");
        }
    }
}
