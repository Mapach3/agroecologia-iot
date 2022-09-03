package com.unla.agroecologiaiot.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SectorModel {
    private long sectorId;

    private String name;
    private String centralizerKey;

    private long gardenId;

    //TODO: AGREGAR LISTADO DE ID DE LOS CROPS
}
