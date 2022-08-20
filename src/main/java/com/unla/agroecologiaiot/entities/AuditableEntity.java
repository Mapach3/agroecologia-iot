package com.unla.agroecologiaiot.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AuditableEntity {
    
    @Column(nullable = false)
    public String CreatedBy = "Prueba";

    @CreatedDate
    @Column(nullable = false, updatable = false)
    public LocalDateTime CreatedAt = LocalDateTime.now();

    @Column(nullable = true)
    public String EditedBy = "";

    @LastModifiedDate
    @Column(nullable = false)
    public LocalDateTime UpdatedAt = LocalDateTime.now();
    
    // @Column(nullable = false)
    // public boolean IsDeleted;

}
