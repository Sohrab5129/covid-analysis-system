package com.mindtree.service;

import com.mindtree.entity.CovidData;
import com.mindtree.entity.CovidDataReport;
import com.mindtree.exceptions.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ICovidData {
    List<CovidData> findAll();
    List<String> getStateName(List<CovidData> covidDataList);
    String getInput();
    List<CovidData> getDistrictByState(List<CovidData> covidDataList);
    boolean validateData(List<CovidData> districtByStateList) throws InvalidStateCodeException;
    String getStartDate();
    Date validateStartDate(String startDate) throws InvalidDateException;
    String getEndDate();
    Date validateEndDate(String endDate) throws InvalidDateException;
    boolean validateStartAndEndDate(Date sDate, Date eDate) throws InvalidDateRangeException;
    List<CovidData> getDataWithinDateRange(Date sDate, Date eDate, List<CovidData> covidDataList);
    boolean validateDataForDateRange(List<CovidData> dataWithinDateRange) throws NoDataFoundException;
    Map<String, List<CovidDataReport>> formatDataForDateRange(List<CovidData> dataWithinDateRange);
    String getFirstStateCode();
    List<CovidData> getDataWithinDateRangeAndState(String firstStateCode, List<CovidData> covidDataList);
    boolean validateFirstStateData(List<CovidData> firstStateData) throws InvalidStateCodeException;
    String getSecondStateCode();
    boolean validateSecondStateData(List<CovidData> secondStateData) throws InvalidStateCodeException;
    Map<String, CovidDataReport> getDataByComparingStateWithinDateRange(List<CovidData> firstStateData, List<CovidData> secondStateData, Date sDate, Date eDate, String firstStateCode, String secondStateCode);
    boolean validateData(Map<String, CovidDataReport> reportMap) throws NoDataFoundException;
    boolean validateOption(String option) throws InvalidOption;
}
