/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.martijncourteaux.supplychainplanner.poc.model;

import com.martijncourteaux.supplychainplanner.generic.model.AbstractTransport;
import com.martijncourteaux.supplychainplanner.persistence.Column;
import com.martijncourteaux.supplychainplanner.persistence.Entity;
import com.martijncourteaux.supplychainplanner.persistence.EntityStringifier;

/**
 *
 * @author martijn
 */
@Entity(prefix = "offer_")
public class PoCTransport extends AbstractTransport<PoCLocation> {

    /* From table transport_offer */
    @Column public long id;
    @Column public int agent;
    @Column public String description;
    @Column public int line;

    @Column public float min_weight;
    @Column public float max_weight;
    @Column public float min_volume;
    @Column public float max_volume;
    @Column public int min_pallets;
    @Column public int max_pallets;

    @Column public double cost_base;
    @Column public double cost_per_kg;
    @Column public double cost_per_m3;
    @Column public double cost_per_pallet;

    @Column public int duration_hours;
    @Column public String required_categories;
    @Column public String rejected_categories;

    /* From table transport_lines */
    @Column(exact_column = "line_code") public String line_code;
    @Column(exact_column = "line_from") public int line_from;
    @Column(exact_column = "line_to") public int line_to;
    @Column(exact_column = "line_modality") public String line_modality;
    @Column(exact_column = "line_distance") public double line_distance;

    /* From table agents */
    @Column(exact_column = "agent_name") public String agent_name;
    @Column(exact_column = "agent_code") public String agent_code;

    /* === Calculated by DBMS in query === */
    @Column(exact_column = "cost_total") public double cost_total;
    @Column(exact_column = "weight_function") public double weight_function;

    @Override
    public double cost() {
        return cost_total;
    }

    @Override
    public double weight() {
        return weight_function;
    }

    @Override
    public String toSingleLineString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%3d", line_from));
        sb.append(" ---[");
        sb.append(line_modality);
        sb.append("   ", 0, 6 - line_modality.length());
        sb.append("]-{ ");
        sb.append(String.format("%12s: "
                + "agent=%2d, "
                + "cost=%7.2f EUR, "
                + "weight=%7.3f, "
                + "duration=%2dh, "
                + "distance=%6.1f km",
                line_code, agent, cost_total,
                weight_function, duration_hours, line_distance));
        sb.append("  }---> ");
        sb.append(String.format("%3d", line_to));
        return sb.toString();
    }

    @Override
    public String toString() {
        return EntityStringifier.entityToString(this);
    }
}
