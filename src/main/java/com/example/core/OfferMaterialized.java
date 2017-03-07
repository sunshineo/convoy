package com.example.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by ssun on 3/7/17.
 */
@Entity(name = "offer_materialized")
public class OfferMaterialized {
    @Id
    @Column(name = "offer_id")
    public String offerId;

    @Column(name = "driver_id")
    public String driverId;

    @Column(name = "shipment_id")
    public String shipmentId;

    @Column(name = "capacity")
    public int capacity;

    @Column(name = "status")
    public String status;
}
