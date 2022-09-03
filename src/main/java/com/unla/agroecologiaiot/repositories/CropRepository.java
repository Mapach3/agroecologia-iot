package com.unla.agroecologiaiot.repositories;

import org.springframework.stereotype.Repository;

import com.unla.agroecologiaiot.entities.Crop;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository("cropRepository")
public interface CropRepository extends JpaRepository<Crop, Long>, JpaSpecificationExecutor<Crop>{
    
    public abstract Optional<Crop> findByName(String name);
    public abstract Optional<Crop> findByCropIdAndIsDeleted(long cropId, boolean isDeleted);
}
