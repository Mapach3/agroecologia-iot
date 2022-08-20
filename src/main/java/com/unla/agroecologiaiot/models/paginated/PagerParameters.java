package com.unla.agroecologiaiot.models.paginated;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.unla.agroecologiaiot.models.paginated.especification.FilterRequest;

import lombok.Getter;

@Getter
public class PagerParameters {

    private int pageSize = 1;
    private int pageIndex = 0;
    private String sortField;
    private String sortDirection;
    private List<FilterRequest> filters;

    public List<FilterRequest> getFilters() {
        if (Objects.isNull(this.filters)) return new ArrayList<>();
        return this.filters;
    }
}
