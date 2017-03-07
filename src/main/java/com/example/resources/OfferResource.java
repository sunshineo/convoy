package com.example.resources;

import com.example.api.OfferDecisionDTO;
import com.example.core.OfferEvent;
import com.example.core.OfferMaterialized;
import com.example.db.DriverMaterializedDao;
import com.example.db.OfferEventDao;
import com.example.db.OfferMaterializedDao;
import io.dropwizard.hibernate.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by ssun on 3/6/17.
 *
 * This can be its own service. I have all endpoints together in one service for simplicity.
 */
@Path("/offer")
@Produces(MediaType.APPLICATION_JSON)
public class OfferResource {
    private static final Logger log = LoggerFactory.getLogger(OfferResource.class);
    private OfferEventDao offerEventDao;
    private OfferMaterializedDao offerMaterializedDao;
    private DriverMaterializedDao driverMaterializedDao;

    public OfferResource(OfferEventDao offerEventDao,
                         OfferMaterializedDao offerMaterializedDao,
                         DriverMaterializedDao driverMaterializedDao){
        this.offerEventDao = offerEventDao;
        this.offerMaterializedDao = offerMaterializedDao;
        this.driverMaterializedDao = driverMaterializedDao;
    }

    @UnitOfWork
    @PUT
    @Path("/{offerId}")
    public void decideOffer(@PathParam("offerId") String offerId, @Valid OfferDecisionDTO offerDecisionDTO) {
        OfferEvent event = new OfferEvent();
        event.offerId = offerId;
        event.eventType = offerDecisionDTO.status;
        this.offerEventDao.create(event);

        // TODO: The proper way is to have the database publish to a event broker such as Kafka or AWS Kinesis.
        this.offerMaterializedDao.handleOfferStatusChangeEvent(event);

        if ("ACCEPT".equalsIgnoreCase(offerDecisionDTO.status))
        {
            removeOtherOffers(event);
        }
    }

    private void removeOtherOffers(final OfferEvent acceptEvent) {
        OfferMaterialized materialized = this.offerMaterializedDao.find(acceptEvent.offerId);
        String shipmentId = materialized.shipmentId;

        List<OfferMaterialized> offersToClear = this.offerMaterializedDao.findByShipmentId(shipmentId);
        for(OfferMaterialized offer : offersToClear) {
            if (offer.offerId == acceptEvent.offerId) {
                continue;
            }
            else {
                OfferEvent removeEvent = new OfferEvent();
                removeEvent.offerId = offer.offerId;
                removeEvent.driverId = offer.driverId;
                removeEvent.shipmentId = offer.shipmentId;
                removeEvent.capacity = offer.capacity;
                removeEvent.eventType = "REMOVE";
                this.offerEventDao.create(removeEvent);

                // TODO: The proper way is to have the database publish to a event broker such as Kafka or AWS Kinesis.
                // However, here we directly manipulate the db for simplicity
                this.offerMaterializedDao.handleOfferStatusChangeEvent(removeEvent);
                this.driverMaterializedDao.handleOfferRemove(removeEvent);
            }
        }
    }
}
