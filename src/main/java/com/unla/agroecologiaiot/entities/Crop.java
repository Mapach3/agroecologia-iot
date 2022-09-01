package com.unla.agroecologiaiot.entities;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "crop")
@EntityListeners(AuditingEntityListener.class)
public class Crop extends AuditableEntity<Long>{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long cropId;

    private String name;

    @ManyToMany(mappedBy = "sectorCrops")
    private Set<Sector> sector;

    // private MetricAcceptationRange metricAcceptationRange;
}
