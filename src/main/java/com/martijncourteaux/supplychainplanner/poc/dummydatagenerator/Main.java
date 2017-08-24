/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.martijncourteaux.supplychainplanner.poc.dummydatagenerator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 *
 * @author martijn
 */
public class Main {

    public static void main(String args[]) throws Exception {
        Class.forName("org.postgresql.Driver");
        String db_uri = "jdbc:postgresql://localhost:5432/poc";
        String db_user = "postgres";
        String db_pass = "";
        try (Connection connection = DriverManager.getConnection(db_uri,
                db_user, db_pass)) {
            System.out.println("Connection made!");

            System.out.print("Type 'yes' if you want to drop all data and regenerate data: ");
            Scanner in = new Scanner(System.in);
            String response = in.nextLine();
            if (response.equals("yes")) {
                regenerate_dummies(connection);
            } else {
                System.out.println("Will not regenerate.");
            }

            print_graph_stats(connection);
        }
    }

    /**
     * Prints some stats of the graph size.
     *
     * @param conn a valid SQL connection.
     * @throws SQLException
     */
    public static void print_graph_stats(Connection conn) throws SQLException {
        long num_loc;
        long num_lines;
        long num_offers;
        long num_agents;
        try (Statement stat = conn.createStatement()) {
            ResultSet rs;

            rs = stat.executeQuery("SELECT COUNT(*) FROM locations");
            rs.next();
            num_loc = rs.getLong(1);

            rs = stat.executeQuery("SELECT COUNT(*) FROM transport_lines");
            rs.next();
            num_lines = rs.getLong(1);

            rs = stat.executeQuery("SELECT COUNT(*) FROM transport_offers");
            rs.next();
            num_offers = rs.getLong(1);

            rs = stat.executeQuery("SELECT COUNT(*) FROM agents");
            rs.next();
            num_agents = rs.getLong(1);
        }

        System.out.printf("Locations     : %6d%n", num_loc);
        System.out.printf("Lines         : %6d%n", num_lines);
        System.out.printf("Offers        : %6d%n", num_offers);
        System.out.printf("Agents        : %6d%n", num_agents);

    }

    private static void regenerate_dummies(Connection conn) throws SQLException {
        drop_dummies(conn);
        generate_dummies(conn);
    }

    private static void drop_dummies(Connection conn) throws SQLException {
        try (Statement stat = conn.createStatement()) {
            stat.executeUpdate("DELETE FROM transport_lines");
            stat.executeUpdate("DELETE FROM transport_offers");
            stat.executeUpdate("DELETE FROM locations");
            stat.executeUpdate("DELETE FROM agents");
            stat.execute("ALTER SEQUENCE locations_location_id_seq RESTART");
            stat.execute("ALTER SEQUENCE transport_lines_line_id_seq RESTART");
            stat.execute("ALTER SEQUENCE transport_offers_offer_id_seq RESTART");
            stat.execute("ALTER SEQUENCE agents_agent_id_seq RESTART");
        }
    }

    private static void generate_dummies(Connection conn) throws SQLException {

        int locations = 100;
        int lines = 50 * locations;
        int offers = 4 * lines;
        int agents = 30;


        AgentGenerator ag = new AgentGenerator();
        ag.generate(conn, agents);
        
        LocationGenerator loc_gen = new LocationGenerator();
        loc_gen.generate(conn, locations);

        LineGenerator lg = new LineGenerator();
        lg.generate(conn, lines, locations);

        OffersGenerator og = new OffersGenerator();
        og.generate(conn, offers, agents, lines);
    }
}
