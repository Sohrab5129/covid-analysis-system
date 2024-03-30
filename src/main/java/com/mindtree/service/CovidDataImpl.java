package com.mindtree.service;

import com.mindtree.dao.ICovidDataDao;
import com.mindtree.entity.CovidData;
import com.mindtree.entity.CovidDataReport;
import com.mindtree.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class CovidDataImpl implements ICovidData {
    @Autowired
    private ICovidDataDao covidDataDao;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private Scanner scanner = new Scanner(System.in);

    private void printMsg(String msg) {
        System.out.print(msg);
    }

    @Override
    public String getInput() {
        return scanner.nextLine();
    }

    @Override
    public List<CovidData> findAll() {
        return covidDataDao.findAll();
    }

    @Override
    public List<String> getStateName(List<CovidData> covidDataList) {
        return covidDataList.stream().map(CovidData::getState).distinct().sorted().collect(Collectors.toList());
    }

    @Override
    public List<CovidData> getDistrictByState(List<CovidData> covidDataList) {
        printMsg("Please enter state code : ");
        String stateCode = getInput();
        return covidDataList.stream()
                .filter(data -> null != data && null != data.getState() && data.getState().trim().equalsIgnoreCase(stateCode))
                .collect(Collectors.toList());
    }

    @Override
    public boolean validateData(List<CovidData> list) throws InvalidStateCodeException {
        if (list.isEmpty()) {
            throw new InvalidStateCodeException("Invalid State code, please check your input");
        }
        return true;
    }

    @Override
    public String getStartDate() {
        printMsg("Please enter Start date (yyyy-MM-dd) : ");
        return getInput();
    }

    @Override
    public Date validateStartDate(String date) throws InvalidDateException {
        try {
            return formatter.parse(date);
        } catch (ParseException e) {
            throw new InvalidDateException("Invalid Start date, please check your input");
        }
    }

    @Override
    public String getEndDate() {
        printMsg("Please enter End date (yyyy-MM-dd) : ");
        return getInput();
    }

    @Override
    public Date validateEndDate(String endDate) throws InvalidDateException {
        try {
            return formatter.parse(endDate);
        } catch (ParseException e) {
            throw new InvalidDateException("Invalid End date, please check your input");
        }
    }

    @Override
    public boolean validateStartAndEndDate(Date sDate, Date eDate) throws InvalidDateRangeException {

        if(sDate.after(eDate)) {
            throw new InvalidDateRangeException("Invalid Date Range, Please check your input");
        }
        return true;
    }

    @Override
    public List<CovidData> getDataWithinDateRange(Date sDate, Date eDate, List<CovidData> covidDataList) {
        return covidDataList.stream()
                .filter(data -> data.getDate().after(sDate) && data.getDate().before((eDate)))
                .sorted(Comparator.comparing(CovidData::getDate)).collect(Collectors.toList());
    }

    @Override
    public boolean validateDataForDateRange(List<CovidData> dataWithinDateRange) throws NoDataFoundException {
        if(dataWithinDateRange.isEmpty()) {
            throw new NoDataFoundException("No data present");
        }
        return true;
    }

    @Override
    public Map<String, List<CovidDataReport>> formatDataForDateRange(List<CovidData> dataWithinDateRange) {
        Map<String, List<CovidDataReport>> reportMap = new HashMap<>();
        for (CovidData data : dataWithinDateRange) {
            String strDate = formatter.format(data.getDate());
            if (reportMap.containsKey(strDate)) {
                List<CovidDataReport> reportList = reportMap.get(strDate);
                AtomicInteger index = new AtomicInteger(-1);
                reportList.forEach(obj -> {
                    if (obj.getState().equalsIgnoreCase(data.getState())) {
                        obj.setCount((Integer.parseInt(data.getConfirmed()) + obj.getCount()));
                        index.getAndIncrement();
                    }
                });
                if(index.get() == -1) {
                    CovidDataReport report = new CovidDataReport();
                    report.setState(data.getState());
                    report.setCount(Integer.parseInt(data.getConfirmed()));
                    reportList.add(report);
                }
                reportMap.put(strDate, reportList);
            } else {
                CovidDataReport report = new CovidDataReport();
                List<CovidDataReport> reportList = new ArrayList<>();
                report.setState(data.getState());
                report.setCount(Integer.parseInt(data.getConfirmed()));
                reportList.add(report);
                reportMap.put(strDate, reportList);
            }
        }
        return reportMap;
    }

    @Override
    public String getFirstStateCode() {
        printMsg("Please enter first state code : ");
        return getInput();
    }

    @Override
    public List<CovidData> getDataWithinDateRangeAndState(String firstStateCode, List<CovidData> covidDataList) {
        return covidDataList.stream()
                .filter(data -> null != data && null != data.getState() && data.getState().trim().equalsIgnoreCase(firstStateCode))
                .collect(Collectors.toList());
    }

    @Override
    public boolean validateFirstStateData(List<CovidData> firstStateData) throws InvalidStateCodeException {
        if(firstStateData.isEmpty()) {
            throw new InvalidStateCodeException("Invalid First State code, please check your input");
        }
        return true;
    }

    @Override
    public String getSecondStateCode() {
        printMsg("Please enter second state code : ");
        return getInput();
    }

    @Override
    public boolean validateSecondStateData(List<CovidData> secondStateData) throws InvalidStateCodeException {
        if(secondStateData.isEmpty()) {
            throw new InvalidStateCodeException("Invalid Second State code, please check your input");
        }
        return true;
    }

    @Override
    public Map<String, CovidDataReport> getDataByComparingStateWithinDateRange(List<CovidData> firstStateData, List<CovidData> secondStateData, Date sDate, Date eDate, String firstStateCode, String secondStateCode) {

        firstStateData = firstStateData.stream()
                .filter(data -> data.getDate().after(sDate) && data.getDate().before((eDate)))
                .sorted(Comparator.comparing(CovidData::getDate).reversed()).collect(Collectors.toList());

        secondStateData = secondStateData.stream()
                .filter(data -> data.getDate().after(sDate) && data.getDate().before((eDate)))
                .sorted(Comparator.comparing(CovidData::getDate).reversed()).collect(Collectors.toList());

        Map<String, CovidDataReport> reportMap = new LinkedHashMap<>();

        firstStateData.forEach(data -> {
            String strDate = formatter.format(data.getDate());
            if(null != reportMap.get(strDate)) {
                CovidDataReport report  = reportMap.get(strDate);
                report.setFConfirmed(String.valueOf(Integer.parseInt(report.getFConfirmed()) + Integer.parseInt(data.getConfirmed())));
                reportMap.put(strDate, report);
            } else {
                CovidDataReport report = new CovidDataReport();
                report.setFState(data.getState());
                report.setFConfirmed(data.getConfirmed());
                report.setSState(secondStateCode);
                report.setSConfirmed(String.valueOf(Integer.valueOf(0)));
                reportMap.put(strDate, report);
            }
        });

        secondStateData.forEach(data -> {
            String strDate = formatter.format(data.getDate());
            if(null != reportMap.get(strDate)) {
                CovidDataReport report  = reportMap.get(strDate);
                report.setSConfirmed(String.valueOf((null != report.getSConfirmed() ? Integer.parseInt(report.getSConfirmed()) : 0 ) + (null != data.getConfirmed() ? Integer.parseInt(data.getConfirmed()) : 0 )));
                report.setSState(data.getState());
                reportMap.put(strDate, report);
            } else {
                CovidDataReport report = new CovidDataReport();
                report.setSState(data.getState());
                report.setSConfirmed(data.getConfirmed());
                report.setFState(firstStateCode);
                report.setFConfirmed(String.valueOf(Integer.valueOf(0)));
                reportMap.put(strDate, report);
            }
        });
        return reportMap;
    }

    @Override
    public boolean validateData(Map<String, CovidDataReport> reportMap) throws NoDataFoundException {
        if(reportMap.size() == 0) {
            throw new NoDataFoundException("No data present");
        }
        return true;
    }

    @Override
    public boolean validateOption(String option) throws InvalidOption {
        if(null == option || option.isEmpty() || !option.matches("\\d+")) {
            throw new InvalidOption("Invalid Option : "+ option);
        }
        return true;
    }
}
