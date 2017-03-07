package com.example.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by ssun on 3/6/17.
 */
public class OfferShipmentResponseDTO {
    @JsonProperty
    public String offerId;

    @JsonProperty
    public String driverId;
}
