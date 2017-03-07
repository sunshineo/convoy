package com.example.db;

import com.example.core.OfferEvent;
import com.example.core.OfferMaterialized;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ssun on 3/7/17.
 */

public class OfferMaterializedDao extends AbstractDAO<OfferMaterialized> {
    /**
     * Creates a new DAO with a given session provider.
     *
     * @param sessionFactory a session provider
     */
    public OfferMaterializedDao(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public void handleOfferCreated(final OfferEvent event) {
        OfferMaterialized driverMaterialized = new OfferMaterialized();
        driverMaterialized.offerId = event.offerId;
        driverMaterialized.shipmentId = event.shipmentId;
        driverMaterialized.driverId = event.driverId;
        driverMaterialized.capacity = event.capacity;
        driverMaterialized.status = "CREATED";
        this.persist(driverMaterialized);
    }

    public void handleOfferStatusChangeEvent(final OfferEvent event) {
        OfferMaterialized offer = this.get(event.offerId);
        offer.status = event.eventType;
        this.persist(offer);
    }

    public OfferMaterialized find(final String id) {
        return this.get(id);
    }

    public List<OfferMaterialized> findByShipmentId(final String shipmentId) {
        Criteria criteria = criteria().add(
                Restrictions.eq("shipmentId", shipmentId)
        );
        return list(criteria);
    }

    public List<OfferMaterialized> findByDriverId(final String driverId) {
        List<String> status = Arrays.asList("ACCEPT", "CREATED");
        Criteria criteria = criteria().add(
                Restrictions.and(
                    Restrictions.eq("driverId", driverId),
                    Restrictions.in("status", status)
                )
        );
        return list(criteria);
    }
}
