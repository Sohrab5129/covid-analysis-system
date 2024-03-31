package com.mindtree.service;

import com.mindtree.dao.ICovidDataDao;
import com.mindtree.entity.CovidData;
import com.mindtree.entity.CovidDataReport;
import com.mindtree.exceptions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CovidDataImplTest {

    @Mock
    private ICovidDataDao mockCovidDataDao;

    @InjectMocks
    private CovidDataImpl covidDataImpl;

    @Mock
    private Scanner scanner = new Scanner(System.in);

    @Test
    void testFindAll() {
        final CovidData covidData1 = new CovidData();
        covidData1.setId(0);
        covidData1.setRecovered("1");
        covidData1.setConfirmed("1");
        covidData1.setState("MH");
        covidData1.setDate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        final List<CovidData> covidData = List.of(covidData1);
        when(mockCovidDataDao.findAll()).thenReturn(covidData);
        final List<CovidData> result = covidDataImpl.findAll();
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void testCovidDataDaoReturnsNoItems() {
        when(mockCovidDataDao.findAll()).thenReturn(Collections.emptyList());
        final List<CovidData> result = covidDataImpl.findAll();
        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void testGetStateName() {
        final CovidData covidData = new CovidData();
        covidData.setId(0);
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        final List<CovidData> covidDataList = List.of(covidData);
        final List<String> result = covidDataImpl.getStateName(covidDataList);
        assertThat(result).isEqualTo(List.of("MH"));
    }

    @Test
    void testGetDistrictByState() {
        final CovidData covidData = new CovidData();
        covidData.setId(0);
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setState("MH");
        covidData.setDistrict("Mumbai");
        covidData.setDate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        final List<CovidData> covidDataList = List.of(covidData);
        Mockito.when(scanner.nextLine()).thenReturn("MH");
        final List<CovidData> result = covidDataImpl.getDistrictByState(covidDataList);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void testValidateData() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setId(0);
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        final List<CovidData> list = List.of(covidData);
        final boolean result = covidDataImpl.validateData(list);
        assertThat(result).isTrue();
    }

    @Test
    void testValidateDataThrowsInvalidStateCodeException() {
        final List<CovidData> list = new ArrayList<>();
        assertThatThrownBy(() -> covidDataImpl.validateData(list)).isInstanceOf(InvalidStateCodeException.class);
    }

    @Test
    void testGetStartDate() {
        Mockito.when(scanner.nextLine()).thenReturn("2020-03-2");
        final String result = covidDataImpl.getStartDate();
        assertThat(result).isEqualTo("2020-03-2");
    }

    @Test
    void testValidateStartDate() throws Exception {
        final Date result = covidDataImpl.validateStartDate("2020-01-01");
        assertThat(result).isEqualTo(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
    }

    @Test
    void testValidateStartDateThrowsInvalidDateException() {
        assertThatThrownBy(() -> covidDataImpl.validateStartDate("20200101")).isInstanceOf(InvalidDateException.class);
    }

    @Test
    void testGetEndDate() {
        Mockito.when(scanner.nextLine()).thenReturn("2020-03-2");
        final String result = covidDataImpl.getEndDate();
        assertThat(result).isEqualTo("2020-03-2");
    }

    @Test
    void testValidateEndDate() throws Exception {
        final Date result = covidDataImpl.validateEndDate("2020-01-1");
        assertThat(result).isEqualTo(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
    }

    @Test
    void testValidateEndDate_ThrowsInvalidDateException() {
        assertThatThrownBy(() -> covidDataImpl.validateEndDate("220020")).isInstanceOf(InvalidDateException.class);
    }

    @Test
    void testValidateStartAndEndDate() throws Exception {
        assertThat(covidDataImpl.validateStartAndEndDate(
                new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime(),
                new GregorianCalendar(2020, Calendar.MARCH, 1).getTime())).isTrue();

        assertThatThrownBy(() -> covidDataImpl.validateStartAndEndDate(
                new GregorianCalendar(2020, Calendar.FEBRUARY, 1).getTime(),
                new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime()))
                .isInstanceOf(InvalidDateRangeException.class);
    }

    @Test
    void testGetDataWithinDateRange() {
        final CovidData covidData = new CovidData();
        covidData.setId(0);
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        final List<CovidData> covidDataList = List.of(covidData);
        final List<CovidData> result = covidDataImpl.getDataWithinDateRange(
                new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(),
                new GregorianCalendar(2021, Calendar.JANUARY, 1).getTime(), covidDataList);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void testValidateDataForDateRange() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setId(0);
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        final List<CovidData> dataWithinDateRange = List.of(covidData);
        final boolean result = covidDataImpl.validateDataForDateRange(dataWithinDateRange);
        assertThat(result).isTrue();
    }

    @Test
    void testValidateDataForDateRangeThrowsNoDataFoundException() {
        final List<CovidData> dataWithinDateRange = new ArrayList<>();

        assertThatThrownBy(() -> covidDataImpl.validateDataForDateRange(dataWithinDateRange))
                .isInstanceOf(NoDataFoundException.class);
    }

    @Test
    void testFormatDataForDateRange() {
        final CovidData covidData = new CovidData();
        covidData.setId(1);
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setState("MH");
        covidData.setTested("1");
        covidData.setDistrict("Mumbai");
        covidData.setDate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        final List<CovidData> dataWithinDateRange = List.of(covidData);

        final CovidDataReport report = new CovidDataReport();
        report.setState("MH");
        report.setCount(1);
        final Map<String, List<CovidDataReport>> expectedResult = Map.ofEntries(Map.entry("2020-01-01", List.of(report)));
        final Map<String, List<CovidDataReport>> result = covidDataImpl.formatDataForDateRange(dataWithinDateRange);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetFirstStateCode() {
        Mockito.when(scanner.nextLine()).thenReturn("MH");
        final String result = covidDataImpl.getFirstStateCode();
        assertThat(result).isEqualTo("MH");
    }

    @Test
    void testGetDataWithinDateRangeAndState() {
        final CovidData covidData = new CovidData();
        covidData.setId(0);
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        final List<CovidData> covidDataList = List.of(covidData);
        final List<CovidData> result = covidDataImpl.getDataWithinDateRangeAndState("MH", covidDataList);

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void testValidateFirstStateData() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setId(0);
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        final List<CovidData> firstStateData = List.of(covidData);
        final boolean result = covidDataImpl.validateFirstStateData(firstStateData);

        assertThat(result).isTrue();
    }

    @Test
    void testValidateFirstStateDataThrowsInvalidStateCodeException() {
        final List<CovidData> firstStateData = new ArrayList<>();
        assertThatThrownBy(() -> covidDataImpl.validateFirstStateData(firstStateData))
                .isInstanceOf(InvalidStateCodeException.class);
    }

    @Test
    void testGetSecondStateCode() {
        Mockito.when(scanner.nextLine()).thenReturn("MH");
        final String result = covidDataImpl.getSecondStateCode();
        assertThat(result).isEqualTo("MH");
    }

    @Test
    void testValidateSecondStateData() throws Exception {
        final CovidData covidData = new CovidData();
        covidData.setId(0);
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        final List<CovidData> secondStateData = List.of(covidData);

        final boolean result = covidDataImpl.validateSecondStateData(secondStateData);
        assertThat(result).isTrue();
    }

    @Test
    void testValidateSecondStateDataThrowsInvalidStateCodeException() {
        final List<CovidData> secondStateData = new ArrayList<>();
        assertThatThrownBy(() -> covidDataImpl.validateSecondStateData(secondStateData))
                .isInstanceOf(InvalidStateCodeException.class);
    }

    @Test
    void testGetDataByComparingStateWithinDateRange() {
        final CovidData covidData = new CovidData();
        covidData.setId(0);
        covidData.setRecovered("1");
        covidData.setConfirmed("1");
        covidData.setState("MH");
        covidData.setDate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        final List<CovidData> firstStateData = List.of(covidData);

        final CovidData covidData1 = new CovidData();
        covidData1.setId(0);
        covidData1.setRecovered("1");
        covidData1.setConfirmed("1");
        covidData1.setState("WB");
        covidData1.setDate(new GregorianCalendar(2020, Calendar.JANUARY, 1).getTime());
        final List<CovidData> secondStateData = List.of(covidData1);

        final CovidDataReport report = new CovidDataReport();
        report.setFState("MH");
        report.setFConfirmed("1");
        report.setSState("WB");
        report.setSConfirmed("1");
        final Map<String, CovidDataReport> expectedResult = Map.ofEntries(Map.entry("2020-01-01", report));

        final Map<String, CovidDataReport> result = covidDataImpl.getDataByComparingStateWithinDateRange(
                firstStateData, secondStateData, new GregorianCalendar(2019, Calendar.JANUARY, 1).getTime(),
                new GregorianCalendar(2021, Calendar.JANUARY, 1).getTime(), "MH", "WB");

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testValidateData2() throws Exception {
        final CovidDataReport report = new CovidDataReport();
        report.setFState("MH");
        report.setFConfirmed("1");
        report.setSState("WB");
        report.setSConfirmed("1");
        final Map<String, CovidDataReport> reportMap = Map.ofEntries(Map.entry("2020-01-01", report));

        final boolean result = covidDataImpl.validateData(reportMap);

        assertThat(result).isTrue();
    }

    @Test
    void testValidateDataThrowsNoDataFoundException() {
        final Map<String, CovidDataReport> reportMap = new HashMap<>();
        assertThatThrownBy(() -> covidDataImpl.validateData(reportMap))
                .isInstanceOf(NoDataFoundException.class);
    }

    @Test
    void testValidateOption() throws Exception {
        assertThat(covidDataImpl.validateOption("1")).isTrue();
        assertThatThrownBy(() -> covidDataImpl.validateOption("one")).isInstanceOf(InvalidOption.class);
    }
}
