package com.example.resources;

import com.codahale.metrics.annotation.Timed;
import com.example.api.DriverCreateDTO;
import com.example.api.DriverResponseDTO;
import com.example.api.OfferDriverResponseDTO;
import com.example.core.DriverEvent;
import com.example.core.OfferMaterialized;
import com.example.db.DriverEventDao;
import com.example.db.DriverMaterializedDao;
import com.example.db.OfferMaterializedDao;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ssun on 3/6/17.
 *
 * This can be its own service. I have all endpoints together in one service for simplicity.
 */
@Path("/driver")
@Produces(MediaType.APPLICATION_JSON)
public class DriverResource {
    private static final Logger log = LoggerFactory.getLogger(DriverResource.class);
    private DriverEventDao driverEventDao;
    private DriverMaterializedDao driverMaterializedDao;
    private OfferMaterializedDao offerMaterializedDao;

    public DriverResource(DriverEventDao driverEventDao,
                          DriverMaterializedDao driverMaterializedDao,
                          OfferMaterializedDao offerMaterializedDao){
        this.driverEventDao = driverEventDao;
        this.driverMaterializedDao = driverMaterializedDao;
        this.offerMaterializedDao = offerMaterializedDao;
    }

    @POST
    @Path("/")
    @Timed
    @UnitOfWork
    public DriverResponseDTO postDriver(@Valid DriverCreateDTO driver) {
        log.info("Received POST to create driver with a capacity {}", driver.capacity);
        UUID uuid = UUID.randomUUID();
        String driverId = uuid.toString();
        log.info("Generated new driver id: {}", driverId);

        DriverEvent event = new DriverEvent();
        event.driverId = driverId;
        event.capacity = driver.capacity;
        event.eventType = "CREATE";
        event = this.driverEventDao.create(event);
        log.info("Created driver CREATE event with event id {}", event.eventId);

        // TODO: The proper way is to have the database publish to a event broker such as Kafka or AWS Kinesis.
        // However, here we directly manipulate the db for simplicity
        log.info("Update the materialized driver view based on the event");
        this.driverMaterializedDao.handleDriverCreateEvent(event);

        DriverResponseDTO response = new DriverResponseDTO();
        response.id = driverId;
        return response;
    }

    @GET
    @Path("/{driverId}")
    @UnitOfWork
    public List<OfferDriverResponseDTO> get(@PathParam("driverId") String driverId) {
        log.info("GET for driver with id {}", driverId);
        List<OfferMaterialized> offers = this.offerMaterializedDao.findByDriverId(driverId);
        log.info("");
        List<OfferDriverResponseDTO> response = new ArrayList<>();
        for(OfferMaterialized offer : offers) {
            OfferDriverResponseDTO dto = new OfferDriverResponseDTO();
            dto.offerId = offer.offerId;
            dto.shipmentId = offer.shipmentId;
            response.add(dto);
        }
        return response;
    }

}
