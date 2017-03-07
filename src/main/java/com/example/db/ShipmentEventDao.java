package com.example.db;

import com.example.core.ShipmentEvent;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

/**
 * Created by ssun on 3/6/17.
 */
public class ShipmentEventDao extends AbstractDAO<ShipmentEvent> {
    public ShipmentEventDao(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public ShipmentEvent create(final ShipmentEvent event) {
        return this.persist(event);
    }
}
