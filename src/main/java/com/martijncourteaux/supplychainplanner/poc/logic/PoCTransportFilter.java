/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.martijncourteaux.supplychainplanner.poc.logic;

import com.martijncourteaux.supplychainplanner.generic.logic.TransportFilter;
import com.martijncourteaux.supplychainplanner.persistence.FieldValueMapper;
import com.martijncourteaux.supplychainplanner.poc.PoCContext;
import com.martijncourteaux.supplychainplanner.poc.model.PoCShipment;
import com.martijncourteaux.supplychainplanner.poc.model.PoCTransport;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author martijn
 */
public class PoCTransportFilter extends TransportFilter<PoCContext, PoCShipment, PoCTransport> {

    @Override
    public String query(PoCContext ctx, PoCShipment cm) {
        String weight_formula = String.format(
                "%f * offer_cost_base + "
                + "%f * offer_cost_per_kg + "
                + "%f * offer_cost_per_m3 + "
                + "%f * offer_cost_per_pallet + "
                + "%f * offer_duration_hours",
                cm.basic_cost_weight,
                cm.cost_per_kg_weight * cm.weight_kg,
                cm.cost_per_m3_weight * cm.volume_m3,
                cm.cost_per_pallet_weight * cm.pallets,
                cm.duration_hours_weight);

        String cost_formula = String.format(
                "offer_cost_base + "
                + "%f * offer_cost_per_kg + "
                + "%f * offer_cost_per_m3 + "
                + "%d * offer_cost_per_pallet",
                cm.weight_kg,
                cm.volume_m3,
                cm.pallets);

        String conditions = String.format(
                "offer_min_weight <= %f AND"
                + " (offer_max_weight IS NULL OR %f <= offer_max_weight) AND "
                + "offer_min_volume <= %f AND"
                + " (offer_max_volume IS NULL OR %f <= offer_max_volume) AND "
                + "offer_min_pallets <= %d AND"
                + " (offer_max_pallets IS NULL OR %d <= offer_max_pallets) ",
                cm.weight_kg, cm.weight_kg,
                cm.volume_m3, cm.volume_m3,
                cm.pallets, cm.pallets);

        if (!cm.allow_ferry) {
            conditions += "AND line_modality != 'ferry' ";
        }
        if (!cm.allow_roads) {
            conditions += "AND line_modality != 'road' ";
        }

        for (int agent_id : cm.disallowed_agents) {
            conditions += "AND offer_agent != " + agent_id;
        }

        String query
                = "SELECT "
                + "(" + cost_formula + ") AS cost_total, "
                + "(" + weight_formula + ") AS weight_function, "
                + "offer_duration_hours, offer_agent, "
                + "line_from, line_to, line_code, line_modality, line_distance "
                + "FROM transport_offers "
                + "INNER JOIN transport_lines ON (line_id = offer_line) "
                + "WHERE " + conditions;

        return query;
    }

    @Override
    public List<PoCTransport> fetch(PoCContext ctx, PoCShipment shipment) throws SQLException {
        try (Statement s = ctx.connection.createStatement()) {
            List<PoCTransport> result = new ArrayList<>(4192);

            String sql = query(ctx, shipment);
            System.out.println(sql);
            try (ResultSet rs = s.executeQuery(sql)) {
                FieldValueMapper<PoCTransport> mapper = new FieldValueMapper<>(PoCTransport.class);
                mapper.prepare(rs);

                while (rs.next()) {
                    PoCTransport t = new PoCTransport();
                    mapper.extract(rs, t);
                    t.source = ctx.getLocation(t.line_from);
                    t.destination = ctx.getLocation(t.line_to);
                    result.add(t);
                }
            }

            return result;
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<PoCTransport> postQueryFilter(PoCContext ctx, List<PoCTransport> transports, PoCShipment shipment) {
        return transports;
    }
    
    
}
