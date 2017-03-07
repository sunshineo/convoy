package com.example.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by ssun on 3/6/17.
 */
public class DriverCreateDTO {
    @JsonProperty
    @NotNull
    @Min(1)
    public int capacity;
}
