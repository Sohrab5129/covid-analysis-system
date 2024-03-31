package com.mindtree.controller;

import com.mindtree.entity.CovidData;
import com.mindtree.entity.CovidDataReport;
import com.mindtree.exceptions.InvalidDateException;
import com.mindtree.exceptions.InvalidDateRangeException;
import com.mindtree.exceptions.InvalidStateCodeException;
import com.mindtree.exceptions.NoDataFoundException;
import com.mindtree.service.ICovidData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MainTest {

    @Mock
    private ICovidData mockCovidData;

    @InjectMocks
    private Main main;

    @Test
    void testShowMenu() {
        main.showMenu();
    }

    @Test
    void testProcessInput() {
        final CovidData covidData = new CovidData();
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setTested("1");
        covidData.setDistrict("Mumbai");
        covidData.setState("MH");
        final List<CovidData> covidDataList = List.of(covidData);
        when(mockCovidData.getStateName(covidDataList)).thenReturn(List.of("MH"));

        main.processInput(1, covidDataList);
    }

    @Test
    void testProcessInputICovidDataGetStateNameReturnsNoItems() {
        final CovidData covidData1 = new CovidData();
        covidData1.setId(0);
        covidData1.setRecovered("1");
        covidData1.setConfirmed("1");
        covidData1.setTested("1");
        covidData1.setDistrict("Mumbai");
        final List<CovidData> covidDataList = List.of(covidData1);
        when(mockCovidData.getStateName(covidDataList)).thenReturn(Collections.emptyList());

        main.processInput(1, covidDataList);
    }

    @Test
    void testProcessInputICovidDataGetDistrictByStateReturnsNoItems() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setId(0);
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setTested("1");
        covidData.setDistrict("Mumbai");

        final List<CovidData> covidDataList = List.of(covidData);
        when(mockCovidData.getDistrictByState(covidDataList)).thenReturn(covidDataList);
        when(mockCovidData.validateData(covidDataList)).thenReturn(false);

        main.processInput(2, covidDataList);
    }

    @Test
    void testProcessInputICovidDataValidateData1ThrowsInvalidStateCodeException() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setTested("1");
        covidData.setState("MH");
        final List<CovidData> covidDataList = List.of(covidData);

        when(mockCovidData.getDistrictByState(covidDataList)).thenReturn(covidDataList);
        when(mockCovidData.validateData(covidDataList)).thenThrow(InvalidStateCodeException.class);

        main.processInput(2, covidDataList);
    }

    @Test
    void testProcessInputICovidDataValidateStartDateThrowsInvalidDateException() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setTested("1");
        covidData.setState("MH");
        final List<CovidData> covidDataList = List.of(covidData);

        when(mockCovidData.getStartDate()).thenReturn("20190101");
        when(mockCovidData.validateStartDate("20190101")).thenThrow(InvalidDateException.class);
        main.processInput(3, covidDataList);
    }

    @Test
    void testProcessInputICovidDataValidateEndDateThrowsInvalidDateException() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setTested("1");
        covidData.setState("MH");

        final List<CovidData> covidDataList = List.of(covidData);
        when(mockCovidData.getStartDate()).thenReturn("2020-01-01");

        final Date date = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateStartDate("2020-01-01")).thenReturn(date);
        when(mockCovidData.getEndDate()).thenReturn("202001-01");
        when(mockCovidData.validateEndDate("202001-01")).thenThrow(InvalidDateException.class);

        main.processInput(3, covidDataList);
    }

    @Test
    void testProcessInputICovidDataValidateStartAndEndDateReturnsTrue() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setTested("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2019, Calendar.MARCH, 1).getTime());
        final List<CovidData> covidDataList = List.of(covidData);

        when(mockCovidData.getStartDate()).thenReturn("2019-01-01");
        final Date startDate = new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateStartDate("2019-01-01")).thenReturn(startDate);
        when(mockCovidData.getEndDate()).thenReturn("2020-01-01");

        final Date endDate = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateEndDate("2020-01-01")).thenReturn(endDate);
        when(mockCovidData.validateStartAndEndDate(startDate, endDate)).thenReturn(true);
        when(mockCovidData.getDataWithinDateRange(startDate, endDate, covidDataList)).thenReturn(covidDataList);
        when(mockCovidData.validateDataForDateRange(covidDataList)).thenReturn(true);

        CovidDataReport report = new CovidDataReport();
        report.setState("MH");
        report.setCount(1);

        Map<String, List<CovidDataReport>> formattedDataForDateRange = new HashMap<>();
        formattedDataForDateRange.put("2019-03-01", List.of(report));
        when(mockCovidData.formatDataForDateRange(covidDataList)).thenReturn(formattedDataForDateRange);

        main.processInput(3, covidDataList);
    }

    @Test
    void testProcessInputICovidDataValidateStartAndEndDateThrowsInvalidDateRangeException() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setTested("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2019, Calendar.MARCH, 1).getTime());
        final List<CovidData> covidDataList = List.of(covidData);
        when(mockCovidData.getStartDate()).thenReturn("2019-01-01");

        final Date date = new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateStartDate("2019-01-01")).thenReturn(date);
        when(mockCovidData.getEndDate()).thenReturn("2020-01-01");

        final Date date1 = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateEndDate("2020-01-01")).thenReturn(date1);
        when(mockCovidData.validateStartAndEndDate(date, date1)).thenThrow(InvalidDateRangeException.class);

        main.processInput(3, covidDataList);
    }

    @Test
    void testProcessInputICovidDataGetDataWithinDateRangeReturnsNoItems() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setTested("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2019, Calendar.MARCH, 1).getTime());
        final List<CovidData> covidDataList = List.of(covidData);
        when(mockCovidData.getStartDate()).thenReturn("2019-01-01");

        final Date date = new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateStartDate("2019-01-01")).thenReturn(date);
        when(mockCovidData.getEndDate()).thenReturn("2020-01-01");

        final Date date1 = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateEndDate("2020-01-01")).thenReturn(date1);
        when(mockCovidData.validateStartAndEndDate(date, date1)).thenReturn(true);
        when(mockCovidData.getDataWithinDateRange(date, date1, covidDataList)).thenReturn(Collections.emptyList());

        main.processInput(3, covidDataList);
    }

    @Test
    void testProcessInputICovidDataValidateDataForDateRangeReturnsTrue() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setTested("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2019, Calendar.MARCH, 1).getTime());
        final List<CovidData> covidDataList = List.of(covidData);
        when(mockCovidData.getStartDate()).thenReturn("2019-01-01");

        final Date date = new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateStartDate("2019-01-01")).thenReturn(date);
        when(mockCovidData.getEndDate()).thenReturn("2020-01-01");

        final Date date1 = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateEndDate("2020-01-01")).thenReturn(date1);
        when(mockCovidData.validateStartAndEndDate(date, date1)).thenReturn(true);
        when(mockCovidData.getDataWithinDateRange(date, date1, covidDataList)).thenReturn(covidDataList);
        when(mockCovidData.validateDataForDateRange(covidDataList)).thenReturn(true);

        final CovidDataReport report = new CovidDataReport();
        report.setCount(1);
        report.setState("MH");
        final Map<String, List<CovidDataReport>> stringListMap = Map.ofEntries(Map.entry("2019-01-01", List.of(report)));
        when(mockCovidData.formatDataForDateRange(covidDataList)).thenReturn(stringListMap);

        main.processInput(3, covidDataList);
    }

    @Test
    void testProcessInputICovidDataValidateDataForDateRangeThrowsNoDataFoundException() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setTested("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2019, Calendar.MARCH, 1).getTime());

        final List<CovidData> covidDataList = List.of(covidData);
        when(mockCovidData.getStartDate()).thenReturn("2019-01-01");

        final Date date = new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateStartDate("2019-01-01")).thenReturn(date);
        when(mockCovidData.getEndDate()).thenReturn("2020-01-01");

        final Date date1 = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateEndDate("2020-01-01")).thenReturn(date1);
        when(mockCovidData.validateStartAndEndDate(date, date1)).thenReturn(true);
        when(mockCovidData.getDataWithinDateRange(date, date1, covidDataList)).thenReturn(covidDataList);
        when(mockCovidData.validateDataForDateRange(covidDataList)).thenThrow(NoDataFoundException.class);

        main.processInput(3, covidDataList);
    }

    @Test
    void testProcessInputICovidDataGetDataWithinDateRangeAndStateReturnsNoItems() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setTested("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2019, Calendar.MARCH, 1).getTime());
        final List<CovidData> covidDataList = List.of(covidData);
        when(mockCovidData.getStartDate()).thenReturn("2019-01-01");

        final Date date = new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateStartDate("2019-01-01")).thenReturn(date);
        when(mockCovidData.getEndDate()).thenReturn("2020-01-01");

        final Date date1 = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateEndDate("2020-01-01")).thenReturn(date1);
        when(mockCovidData.validateStartAndEndDate(date, date1)).thenReturn(true);
        when(mockCovidData.getFirstStateCode()).thenReturn("MH");
        when(mockCovidData.getDataWithinDateRangeAndState("MH", covidDataList)).thenReturn(Collections.emptyList());

        main.processInput(4, covidDataList);
    }

    @Test
    void testProcessInputICovidDataValidateFirstStateDataReturnsTrue() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setTested("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2019, Calendar.MARCH, 1).getTime());

        final List<CovidData> covidDataList = List.of(covidData);
        when(mockCovidData.getStartDate()).thenReturn("2019-01-01");

        final Date date = new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateStartDate("2019-01-01")).thenReturn(date);
        when(mockCovidData.getEndDate()).thenReturn("2020-01-01");

        final Date date1 = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateEndDate("2020-01-01")).thenReturn(date1);
        when(mockCovidData.validateStartAndEndDate(date, date1)).thenReturn(true);
        when(mockCovidData.getFirstStateCode()).thenReturn("MH");
        when(mockCovidData.getDataWithinDateRangeAndState("MH", covidDataList)).thenReturn(covidDataList);
        when(mockCovidData.validateFirstStateData(covidDataList)).thenReturn(true);
        when(mockCovidData.getSecondStateCode()).thenReturn("WB");
        when(mockCovidData.getDataWithinDateRangeAndState("WB", covidDataList)).thenReturn(covidDataList);
        when(mockCovidData.validateSecondStateData(covidDataList)).thenReturn(true);

        main.processInput(4, covidDataList);
    }

    @Test
    void testProcessInputICovidDataValidateFirstStateDataThrowsInvalidStateCodeException() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setTested("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2019, Calendar.MARCH, 1).getTime());

        final List<CovidData> covidDataList = List.of(covidData);
        when(mockCovidData.getStartDate()).thenReturn("2019-01-01");

        final Date date = new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateStartDate("2019-01-01")).thenReturn(date);
        when(mockCovidData.getEndDate()).thenReturn("2020-01-01");

        final Date date1 = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateEndDate("2020-01-01")).thenReturn(date1);
        when(mockCovidData.validateStartAndEndDate(date, date1)).thenReturn(true);
        when(mockCovidData.getFirstStateCode()).thenReturn("MH");
        when(mockCovidData.getDataWithinDateRangeAndState("MH", covidDataList)).thenReturn(covidDataList);
        when(mockCovidData.validateFirstStateData(covidDataList)).thenThrow(InvalidStateCodeException.class);

        main.processInput(4, covidDataList);
    }

    @Test
    void testProcessInputICovidDataValidateSecondStateDataReturnsTrue() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setTested("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2019, Calendar.MARCH, 1).getTime());
        final List<CovidData> covidDataList = List.of(covidData);
        when(mockCovidData.getStartDate()).thenReturn("2019-01-01");

        final Date date = new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateStartDate("2019-01-01")).thenReturn(date);
        when(mockCovidData.getEndDate()).thenReturn("2020-01-01");

        final Date date1 = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateEndDate("2020-01-01")).thenReturn(date1);
        when(mockCovidData.validateStartAndEndDate(date, date1)).thenReturn(true);

        when(mockCovidData.getFirstStateCode()).thenReturn("MH");
        when(mockCovidData.getDataWithinDateRangeAndState("MH", covidDataList)).thenReturn(covidDataList);
        when(mockCovidData.validateFirstStateData(covidDataList)).thenReturn(true);

        when(mockCovidData.getSecondStateCode()).thenReturn("WB");
        when(mockCovidData.getDataWithinDateRangeAndState("WB", covidDataList)).thenReturn(covidDataList);
        when(mockCovidData.validateSecondStateData(covidDataList)).thenReturn(true);

        final CovidDataReport report = new CovidDataReport();
        report.setFState("MH");
        report.setFConfirmed("1");
        report.setSState("WB");
        report.setSConfirmed("1");
        report.setCount(0);

        final Map<String, CovidDataReport> reportMap = Map.ofEntries(Map.entry("2019-01-01", report));
        when(mockCovidData.getDataByComparingStateWithinDateRange(covidDataList, covidDataList, date, date1, "MH", "WB")).thenReturn(reportMap);
        final Map<String, CovidDataReport> reportMap1 = Map.ofEntries(Map.entry("2019-01-01", report));
        when(mockCovidData.validateData(reportMap1)).thenReturn(true);

        main.processInput(4, covidDataList);
    }

    @Test
    void testProcessInputICovidDataValidateSecondStateDataThrowsInvalidStateCodeException() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setTested("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2019, Calendar.MARCH, 1).getTime());
        final List<CovidData> covidDataList = List.of(covidData);
        when(mockCovidData.getStartDate()).thenReturn("2019-01-01");

        final Date date = new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateStartDate("2019-01-01")).thenReturn(date);
        when(mockCovidData.getEndDate()).thenReturn("2020-01-01");

        final Date date1 = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateEndDate("2020-01-01")).thenReturn(date1);
        when(mockCovidData.validateStartAndEndDate(date, date1)).thenReturn(true);

        when(mockCovidData.getFirstStateCode()).thenReturn("MH");
        when(mockCovidData.getDataWithinDateRangeAndState("MH", covidDataList)).thenReturn(covidDataList);
        when(mockCovidData.validateFirstStateData(covidDataList)).thenReturn(true);

        when(mockCovidData.getSecondStateCode()).thenReturn("WB");
        when(mockCovidData.getDataWithinDateRangeAndState("WB", covidDataList)).thenReturn(covidDataList);
        when(mockCovidData.validateSecondStateData(covidDataList)).thenThrow(InvalidStateCodeException.class);

        main.processInput(4, covidDataList);
    }

    @Test
    void testProcessInputICovidDataValidateData2ThrowsNoDataFoundException() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setTested("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2019, Calendar.MARCH, 1).getTime());
        final List<CovidData> covidDataList = List.of(covidData);
        when(mockCovidData.getStartDate()).thenReturn("2019-01-01");

        final Date date = new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateStartDate("2019-01-01")).thenReturn(date);
        when(mockCovidData.getEndDate()).thenReturn("2020-01-01");

        final Date date1 = new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime();
        when(mockCovidData.validateEndDate("2020-01-01")).thenReturn(date1);
        when(mockCovidData.validateStartAndEndDate(date, date1)).thenReturn(true);

        when(mockCovidData.getFirstStateCode()).thenReturn("MH");
        when(mockCovidData.getDataWithinDateRangeAndState("MH", covidDataList)).thenReturn(covidDataList);
        when(mockCovidData.validateFirstStateData(covidDataList)).thenReturn(true);

        when(mockCovidData.getSecondStateCode()).thenReturn("WB");
        when(mockCovidData.getDataWithinDateRangeAndState("WB", covidDataList)).thenReturn(covidDataList);
        when(mockCovidData.validateSecondStateData(covidDataList)).thenReturn(true);

        Map<String, CovidDataReport> reportMap = new HashMap<>();
        when(mockCovidData.getDataByComparingStateWithinDateRange(covidDataList, covidDataList, date, date1, "MH","WB")).thenReturn(reportMap);
        when(mockCovidData.validateData(reportMap)).thenThrow(new NoDataFoundException("No Data"));
        main.processInput(4, covidDataList);
    }

    @Test
    void testProcessOptionICovidDataReturnsNoItems() {
        final List<CovidData> covidDataList = new ArrayList<>();
        when(mockCovidData.getStateName(covidDataList)).thenReturn(Collections.emptyList());
        main.processOption1(covidDataList);
    }

    @Test
    void testPrintResult() {
        main.printResult(List.of("MH", "WB"));
    }
}
