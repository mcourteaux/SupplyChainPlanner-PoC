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

    public void generate(Connection conn, int numLines, int numLocations) throws SQLException {
        String query
                = "INSERT INTO transport_lines (line_from, line_to, line_distance, line_code, line_modality) VALUES(?, ?, ?, ?, ?);";
        try (PreparedStatement stat = conn.prepareStatement(query)) {
            Random random = new Random();
            
            for (int i = 0; i < numLines; ++i) {
                int loc_from = random.nextInt(numLocations);
                int loc_to = (loc_from + random.nextInt(20) + numLocations - 10) % numLocations;
                if (loc_to == loc_from) loc_to++;
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

        query = "UPDATE transport_lines "
                + "SET line_distance=subquery.calced_dist "
                + "FROM ("
                + "   SELECT "
                + "     0.01745329252 * SQRT("
                + "        POW(lfrom.location_lon - lto.location_lon, 2) + "
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
