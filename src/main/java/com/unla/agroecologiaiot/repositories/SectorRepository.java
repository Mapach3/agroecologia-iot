package com.unla.agroecologiaiot.repositories;

import org.springframework.stereotype.Repository;

import com.unla.agroecologiaiot.entities.Garden;
import com.unla.agroecologiaiot.entities.Sector;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository("sectorRepository")
public interface SectorRepository extends JpaRepository<Sector, Long>{
    
    public abstract List<Sector> findByGarden(Garden garden);
}
