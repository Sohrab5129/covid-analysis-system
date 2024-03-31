package com.mindtree.dao;

import com.mindtree.entity.CovidData;
import com.mindtree.repository.CovidDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CovidDataDaoImplTest {

    @Mock
    private CovidDataRepository mockCovidDataRepository;

    @InjectMocks
    private CovidDataDaoImpl covidDataDao;

    @Test
    void testFindAll() {
        final CovidData data = new CovidData();
        data.setId(1);
        data.setRecovered("1");
        data.setConfirmed("1");
        data.setTested("1");
        data.setDistrict("Mumbai");
        data.setState("MH");

        final List<CovidData> covidData = List.of(data);
        when(mockCovidDataRepository.findAll()).thenReturn(covidData);

        final List<CovidData> result = covidDataDao.findAll();
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void testFindAll_CovidDataRepositoryReturnsNoItems() {
        when(mockCovidDataRepository.findAll()).thenReturn(Collections.emptyList());
        final List<CovidData> result = covidDataDao.findAll();
        assertThat(result).isEqualTo(Collections.emptyList());
    }
}
