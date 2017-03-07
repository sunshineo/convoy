package com.example;

import com.example.core.DriverEvent;
import com.example.core.DriverMaterialized;
import com.example.core.OfferEvent;
import com.example.core.OfferMaterialized;
import com.example.core.ShipmentEvent;
import com.example.db.DriverEventDao;
import com.example.db.DriverMaterializedDao;
import com.example.db.OfferEventDao;
import com.example.db.OfferMaterializedDao;
import com.example.db.ShipmentEventDao;
import com.example.resources.DriverResource;
import com.example.resources.OfferResource;
import com.example.resources.ShipmentResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class ConvoyApplication extends Application<ConvoyConfiguration> {

    private final HibernateBundle<ConvoyConfiguration> hibernate =
            new HibernateBundle<ConvoyConfiguration>(DriverEvent.class, DriverMaterialized.class, OfferEvent.class, OfferMaterialized.class, ShipmentEvent.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(ConvoyConfiguration configuration) {
                    return configuration.database;
                }
            };

    public static void main(final String[] args) throws Exception {
        new ConvoyApplication().run(args);
    }

    @Override
    public String getName() {
        return "convoy";
    }

    @Override
    public void initialize(final Bootstrap<ConvoyConfiguration> bootstrap) {
        bootstrap.addBundle(new MigrationsBundle<ConvoyConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(ConvoyConfiguration configuration) {
                return configuration.database;
            }
        });
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(final ConvoyConfiguration configuration,
                    final Environment environment) {
        DriverEventDao driverEventDao = new DriverEventDao(hibernate.getSessionFactory());
        DriverMaterializedDao driverMaterializedDao = new DriverMaterializedDao(hibernate.getSessionFactory());
        OfferEventDao offerEventDao = new OfferEventDao(hibernate.getSessionFactory());
        OfferMaterializedDao offerMaterializedDao = new OfferMaterializedDao(hibernate.getSessionFactory());
        ShipmentEventDao shipmentEventDao = new ShipmentEventDao(hibernate.getSessionFactory());

        environment.jersey().register(new DriverResource(driverEventDao, driverMaterializedDao, offerMaterializedDao));
        environment.jersey().register(new OfferResource(offerEventDao, offerMaterializedDao, driverMaterializedDao));
        environment.jersey().register(new ShipmentResource(shipmentEventDao,driverMaterializedDao,offerEventDao,offerMaterializedDao));
    }

}
