package com.unla.agroecologiaiot.models.paginated;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaginatedList<T> {
    
    private List<T> list;
    private long count;
    private long index;
}
