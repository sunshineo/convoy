package com.example.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by ssun on 3/6/17.
 */
@Entity(name = "shipment_events")
public class ShipmentEvent {
    @Column(name = "event_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long eventId;

    @Column(name = "shipment_id")
    public String shipmentId;

    @Column(name = "event_type")
    public String eventType;

    @Column(name = "capacity")
    public int capacity;
}
