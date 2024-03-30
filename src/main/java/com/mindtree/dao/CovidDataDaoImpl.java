package com.mindtree.dao;

import com.mindtree.entity.CovidData;
import com.mindtree.repository.CovidDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CovidDataDaoImpl implements ICovidDataDao {

    @Autowired
    private CovidDataRepository covidDataRepository;

    @Override
    public List<CovidData> findAll() {
        return covidDataRepository.findAll();
    }
}
