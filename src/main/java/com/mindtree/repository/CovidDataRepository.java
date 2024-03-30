package com.mindtree.repository;

import com.mindtree.entity.CovidData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CovidDataRepository extends JpaRepository<CovidData, Integer> {

}
