package com.example.resources;

import com.codahale.metrics.annotation.Timed;
import com.example.api.OfferShipmentResponseDTO;
import com.example.api.ShipmentCreateDTO;
import com.example.api.ShipmentCreateResponseDTO;
import com.example.api.ShipmentGetResponseDTO;
import com.example.core.DriverMaterialized;
import com.example.core.OfferEvent;
import com.example.core.OfferMaterialized;
import com.example.core.ShipmentEvent;
import com.example.db.DriverMaterializedDao;
import com.example.db.OfferEventDao;
import com.example.db.OfferMaterializedDao;
import com.example.db.ShipmentEventDao;
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
@Path("/shipment")
@Produces(MediaType.APPLICATION_JSON)
public class ShipmentResource {
    private static final Logger log = LoggerFactory.getLogger(ShipmentResource.class);
    private ShipmentEventDao shipmentEventDao;
    private DriverMaterializedDao driverMaterializedDao;
    private OfferEventDao offerEventDao;
    private OfferMaterializedDao offerMaterializedDao;

    public ShipmentResource(ShipmentEventDao shipmentEventDao,
                            DriverMaterializedDao driverMaterializedDao,
                            OfferEventDao offerEventDao,
                            OfferMaterializedDao offerMaterializedDao){
        this.shipmentEventDao = shipmentEventDao;
        this.driverMaterializedDao = driverMaterializedDao;
        this.offerEventDao = offerEventDao;
        this.offerMaterializedDao = offerMaterializedDao;
    }

    @POST
    @Path("/")
    @Timed
    @UnitOfWork
    public ShipmentCreateResponseDTO postDriver(@Valid ShipmentCreateDTO shipment) {
        log.info("Received POST to create shipment with a capacity {}", shipment.capacity);
        UUID uuid = UUID.randomUUID();
        String shipmentId = uuid.toString();
        log.info("Generated new shipment id: {}", shipmentId);

        ShipmentEvent event = new ShipmentEvent();
        event.shipmentId = shipmentId;
        event.eventType = "CREATE";
        event.capacity = shipment.capacity;
        event = this.shipmentEventDao.create(event);
        log.info("Created shipment CREATE event with event id {}", event.eventId);

        // TODO: The proper way is to have the database publish to a event broker such as Kafka or AWS Kinesis.
        // Then we have a consumer that consumes the event, run the offer creation logic based on the driver_materialized view,
        // save created offers in db and trigger offer creation events which will then trigger update to driver_materialized view.
        // However, here we directly call the matching logic for simplicity
        log.info("Call matching logic to get offers");
        List<OfferShipmentResponseDTO> offers = createOffers(event);

        ShipmentCreateResponseDTO response = new ShipmentCreateResponseDTO();
        response.id = shipmentId;
        response.offers = offers;
        return response;
    }

    private List<OfferShipmentResponseDTO> createOffers(final ShipmentEvent shipmentEvent) {
        // TODO: This should be handling a stream of events
        log.info("Call driverMaterializedDao to get top 10 drivers for the offer.");
        List<DriverMaterialized> top10 = this.driverMaterializedDao.getTop10(shipmentEvent);

        List<OfferShipmentResponseDTO> response = new ArrayList<>();
        for(DriverMaterialized driver : top10) {
            log.info("Processing for driver {}", driver.driverId);
            String offerId = UUID.randomUUID().toString();
            log.info("Generated offerId {}", offerId);

            OfferEvent offerEvent = new OfferEvent();
            offerEvent.offerId = offerId;
            offerEvent.driverId = driver.driverId;
            offerEvent.shipmentId = shipmentEvent.shipmentId;
            offerEvent.capacity = shipmentEvent.capacity;
            offerEvent.eventType = "CREATE";
            offerEvent = this.offerEventDao.create(offerEvent);
            log.info("Created offer CREATE event with event id {}", offerEvent.eventId);

            OfferShipmentResponseDTO dto = new OfferShipmentResponseDTO();
            dto.driverId = offerEvent.driverId;
            dto.offerId = offerEvent.offerId;
            response.add(dto);

            // TODO: The proper way is to have the database publish to a event broker such as Kafka or AWS Kinesis.
            // However, here we directly manipulate the db for simplicity
            this.driverMaterializedDao.handleOfferCreated(offerEvent);
            this.offerMaterializedDao.handleOfferCreated(offerEvent);
        }
        return response;
    }

    @GET
    @Path("/{shipmentId}")
    @UnitOfWork
    public ShipmentGetResponseDTO get(@PathParam("shipmentId") String shipmentId) {
        log.info("Looking for offers for shipment {}", shipmentId);
        List<OfferMaterialized> offers = this.offerMaterializedDao.findByShipmentId(shipmentId);
        log.info("Found {} offers", offers.size());
        ShipmentGetResponseDTO response = new ShipmentGetResponseDTO();
        response.offers = new ArrayList<>();
        for(OfferMaterialized offer : offers) {
            OfferShipmentResponseDTO dto = new OfferShipmentResponseDTO();
            dto.driverId = offer.driverId;
            dto.offerId = offer.offerId;

            log.info("Offer {} has status {}", offer.offerId, offer.status);
            if ("ACCEPT".equalsIgnoreCase(offer.status)) {
                response.offers = new ArrayList<>();
                response.offers.add(dto);
                response.accepted = true;
                return response;
            }
            else if ("CREATED".equalsIgnoreCase(offer.status)) {
                response.offers.add(dto);
            }
        }
        return response;
    }
}
