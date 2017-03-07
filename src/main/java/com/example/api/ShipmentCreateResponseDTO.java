package com.example.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by ssun on 3/6/17.
 */
public class ShipmentCreateResponseDTO {
    @JsonProperty
    public String id;

    @JsonProperty
    public List<OfferShipmentResponseDTO> offers;
}
