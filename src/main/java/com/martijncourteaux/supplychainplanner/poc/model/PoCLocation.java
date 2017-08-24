/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.martijncourteaux.supplychainplanner.poc.model;

import com.martijncourteaux.supplychainplanner.generic.model.AbstractLocation;
import com.martijncourteaux.supplychainplanner.persistence.Column;
import com.martijncourteaux.supplychainplanner.persistence.Entity;

/**
 *
 * @author martijn
 */
@Entity(prefix = "location_")
public class PoCLocation extends AbstractLocation {
    @Column public long id;
    @Column public double lat;
    @Column public double lon;
    @Column public String desc;

    @Override
    public boolean isCompatible(AbstractLocation other) {
        if (other instanceof PoCLocation) {
            return ((PoCLocation) other).id == id;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("LOC_%03d (%6.3f; %6.3f)", id, lat, lon);
    }
}
