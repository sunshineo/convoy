package com.example.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by ssun on 3/7/17.
 */
@Entity(name = "driver_materialized")
public class DriverMaterialized {
    @Id
    @Column(name = "driver_id")
    public String driverId;

    @Column(name = "capacity")
    public int capacity;

    @Column(name = "offers_count")
    public int offersCount;
}
