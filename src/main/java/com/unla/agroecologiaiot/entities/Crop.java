package com.unla.agroecologiaiot.entities;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.ForeignKey;

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
public class Crop extends AuditableEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long cropId;

    private String name;

    @ManyToMany(mappedBy = "crops")
    private Set<Sector> sectors;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownerUserId", nullable = false, foreignKey = @ForeignKey(name = "FK_Crop_User"))
    private ApplicationUser owner;

    // private MetricAcceptationRange metricAcceptationRange;
}
