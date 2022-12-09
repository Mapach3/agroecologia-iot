package com.unla.agroecologiaiot.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.unla.agroecologiaiot.entities.MetricReading;

@Repository("metricReadingRepository")
public interface MetricReadingRepository
                extends JpaRepository<MetricReading, Long>, JpaSpecificationExecutor<MetricReading> {

        public abstract Optional<MetricReading> findByValueType(String valueType);

}



