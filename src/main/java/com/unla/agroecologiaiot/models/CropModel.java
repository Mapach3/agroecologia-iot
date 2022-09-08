package com.unla.agroecologiaiot.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CropModel {

    private long cropId;

    private String name;
    private long ownerId;
    @JsonIgnore
    private Date createdAt;
}
