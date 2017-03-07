package com.example.db;

import com.example.core.DriverEvent;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import javax.persistence.Entity;

/**
 * Created by ssun on 3/6/17.
 */
@Entity(name = "driver_event")
public class DriverEventDao extends AbstractDAO<DriverEvent> {
    /**
     * Creates a new DAO with a given session provider.
     *
     * @param sessionFactory a session provider
     */
    public DriverEventDao(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public DriverEvent create(final DriverEvent event) {
        return this.persist(event);
    }
}
