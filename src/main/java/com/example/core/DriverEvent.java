package com.example.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by ssun on 3/6/17.
 */
@Entity(name = "driver_events")
public class DriverEvent {
    @Column(name = "event_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long eventId;

    @Column(name = "driver_id")
    public String driverId;

    @Column(name = "event_type")
    public String eventType;

    @Column(name = "capacity")
    public int capacity;
}
