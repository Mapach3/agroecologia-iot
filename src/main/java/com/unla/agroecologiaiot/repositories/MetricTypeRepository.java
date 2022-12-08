package com.unla.agroecologiaiot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.unla.agroecologiaiot.entities.MetricType;

@Repository("metricTypeRepository")
public interface MetricTypeRepository extends JpaRepository<MetricType, Long>{
    
    public abstract MetricType findByCode(String code);
}
