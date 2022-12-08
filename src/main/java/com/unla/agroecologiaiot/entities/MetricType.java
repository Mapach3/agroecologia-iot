package com.unla.agroecologiaiot.entities;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class MetricType {

    @Id
    private String code;
    private String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "metricType")
    private Set<MetricReading> metricReadings;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "metricType")
    private Set<MetricAcceptationRange> metricAcceptationRanges;
}
