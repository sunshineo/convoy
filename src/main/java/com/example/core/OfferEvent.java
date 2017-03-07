package com.example.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by ssun on 3/7/17.
 */
@Entity(name = "offer_events")
public class OfferEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    public long eventId;

    @Column(name = "offer_id")
    public String offerId;

    @Column(name = "driver_id")
    public String driverId;

    @Column(name = "shipment_id")
    public String shipmentId;

    @Column(name = "event_type")
    public String eventType;

    @Column(name = "capacity")
    public int capacity;
}
