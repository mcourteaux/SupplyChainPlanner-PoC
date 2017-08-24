/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.martijncourteaux.supplychainplanner.poc.dummydatagenerator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

/**
 *
 * @author martijn
 */
public class AgentGenerator {
    
    public void generate(Connection conn, int numAgents) throws SQLException {
        String query
                = "INSERT INTO agents (agent_name, agent_code, agent_headquarters_country_code) VALUES(?, ?, ?);";
        try (PreparedStatement stat = conn.prepareStatement(query)) {
            Random random = new Random();
            
            String[] ccs = {"FN", "FR", "NL", "IT", "ES", "DE", "UK" };
            for (int i = 0; i < numAgents; ++i) {
                String name = String.format("AGENT_%03d", i);
                String cc = ccs[random.nextInt(ccs.length)];
                stat.setString(1, name);
                stat.setString(2, name);
                stat.setString(3, cc);
                
                stat.addBatch();
            }
            stat.executeBatch();
            stat.clearBatch();
        }
        System.out.println("Insert agents: Done");
    }
}
