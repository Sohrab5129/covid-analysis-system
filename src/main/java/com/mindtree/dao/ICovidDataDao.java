package com.mindtree.dao;

import com.mindtree.entity.CovidData;

import java.util.List;

public interface ICovidDataDao {
    List<CovidData> findAll();
}
