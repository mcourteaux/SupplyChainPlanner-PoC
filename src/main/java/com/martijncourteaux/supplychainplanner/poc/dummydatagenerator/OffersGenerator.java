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
import java.util.Random;

public class OffersGenerator {

    public void generate(Connection conn, int numOffers, int numAgents, int numLines)
            throws SQLException {
        String query
                = "INSERT INTO transport_offers (offer_agent, offer_description, offer_line, offer_min_weight, offer_max_weight, offer_min_volume, offer_max_volume, offer_min_pallets, offer_max_pallets, offer_cost_base, offer_cost_per_kg, offer_cost_per_m3, offer_cost_per_pallet, offer_duration_hours, offer_required_categories, offer_rejected_categories) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement stat = conn.prepareStatement(query);

        Random random = new Random();

        for (int i = 0; i < numOffers; ++i) {
            stat.setInt(1, random.nextInt(numAgents) + 1);
            stat.setString(2, String.format("OFFER_%05d", i + 1));
            if (i < 2 * numLines) {
                stat.setInt(3, (i % numLines) + 1);
            } else {
                stat.setInt(3, random.nextInt(numLines) + 1);
            }

            // Weight
            if (random.nextBoolean()) {
                stat.setFloat(4, 0.0f);
                stat.setNull(5, java.sql.Types.FLOAT);
            } else {
                stat.setFloat(4, random.nextFloat() * 100.0f);
                stat.setFloat(5, 200.0f + random.nextFloat() * 3000.0f);
            }

            // Volume
            if (random.nextBoolean()) {
                stat.setFloat(6, 0.0f);
                stat.setNull(7, java.sql.Types.FLOAT);
            } else {
                stat.setFloat(6, random.nextFloat() * 20.0f);
                stat.setFloat(7, 30.0f + random.nextFloat() * 50.0f);
            }

            // Pallets
            if (random.nextInt(10) == 0) {
                stat.setInt(8, 0);
                stat.setNull(9, java.sql.Types.INTEGER);
            } else {
                stat.setInt(8, random.nextInt(20));
                stat.setInt(9, 30 + random.nextInt(50));
            }

            // Costs
            stat.setDouble(10, random.nextDouble() * 20.0);
            double cost_kg = 0.0;
            double cost_m3 = 0.0;
            double cost_pa = 0.0;
            switch (random.nextInt(3)) {
                case 0:
                    cost_kg = random.nextDouble() * 10.0;
                    break;
                case 1:
                    cost_m3 = random.nextDouble() * 10.0;
                    break;
                case 2:
                    cost_pa = random.nextDouble() * 10.0;
                    break;
            }
            stat.setDouble(11, cost_kg);
            stat.setDouble(12, cost_m3);
            stat.setDouble(13, cost_pa);

            // Duration
            stat.setInt(14, 1 + Math.abs((int) (random.nextGaussian() * 10.0)));

            // Categories
            stat.setNull(15, java.sql.Types.VARCHAR);
            stat.setNull(16, java.sql.Types.VARCHAR);

            stat.addBatch();
            if (i % 100 == 0) {
                stat.executeBatch();
                System.out.println("Inserted offers " + i);
                stat.clearBatch();
            }
        }
        stat.executeBatch();
        stat.clearBatch();
        System.out.println("Done");
    }
}
