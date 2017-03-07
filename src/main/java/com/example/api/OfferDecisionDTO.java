package com.example.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Created by ssun on 3/7/17.
 */
public class OfferDecisionDTO {
    @JsonProperty
    @NotNull
    public String status;
}
