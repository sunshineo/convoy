package com.example.db;

import com.example.core.OfferEvent;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

/**
 * Created by ssun on 3/6/17.
 */
public class OfferEventDao extends AbstractDAO<OfferEvent>{
    /**
     * Creates a new DAO with a given session provider.
     *
     * @param sessionFactory a session provider
     */
    public OfferEventDao(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public OfferEvent create(final OfferEvent event) {
        return this.persist(event);
    }
}
