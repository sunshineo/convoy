package com.example.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by ssun on 3/7/17.
 */
public class ShipmentGetResponseDTO {
    @JsonProperty
    public boolean accepted;

    @JsonProperty
    public List<OfferShipmentResponseDTO> offers;
}
