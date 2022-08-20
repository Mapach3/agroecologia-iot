package com.unla.agroecologiaiot.models.paginated;

import lombok.Getter;

@Getter
public class PagerParametersModel {

    private int pageSize = 1;
    private int pageIndex = 0;
    private String sortField;
    private String sortDirection;
}
