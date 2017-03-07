package com.example.db;

import com.example.core.DriverEvent;
import com.example.core.DriverMaterialized;
import com.example.core.OfferEvent;
import com.example.core.ShipmentEvent;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by ssun on 3/7/17.
 *
 */
public class DriverMaterializedDao extends AbstractDAO<DriverMaterialized> {
    /**
     * Creates a new DAO with a given session provider.
     *
     * @param sessionFactory a session provider
     */
    public DriverMaterializedDao(final SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    // Imagine this class is a worker that consumes these events
    public void handleDriverCreateEvent(final DriverEvent event) {
        DriverMaterialized driverMaterialized = new DriverMaterialized();
        driverMaterialized.driverId = event.driverId;
        driverMaterialized.capacity = event.capacity;
        driverMaterialized.offersCount = 0;
        this.persist(driverMaterialized);
    }

    public List<DriverMaterialized> getTop10(final ShipmentEvent event) {
        Criteria criteria = criteria().add(
                Restrictions.ge("capacity", event.capacity)
        ).addOrder(
                Order.asc("offersCount")
        ).setMaxResults(10);
        return list(criteria);
    }

    public void handleOfferCreated(final OfferEvent event) {
        String driverId = event.driverId;
        DriverMaterialized driver = this.get(driverId);
        driver.capacity -= event.capacity;
        driver.offersCount ++;
        this.persist(driver);
    }

    public void handleOfferRemove(final OfferEvent event) {
        String driverId = event.driverId;
        DriverMaterialized driver = this.get(driverId);
        driver.capacity += event.capacity;
        driver.offersCount --;
        this.persist(driver);
    }
}
