package com.mindtree.controller;

import com.mindtree.entity.CovidData;
import com.mindtree.entity.CovidDataReport;
import com.mindtree.exceptions.*;
import com.mindtree.service.ICovidData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class Main {
    @Autowired
    private ICovidData covidData;

    @EventListener(ApplicationReadyEvent.class)
    public void covidAnalysis() {
        startAnalysis();
    }

    public void startAnalysis() {
        List<CovidData> covidDataList = covidData.findAll();
        while (true) {
            showMenu();
            String option = covidData.getInput();
            boolean status = false;
            try {
                status = covidData.validateOption(option);
            } catch (InvalidOption e) {
                System.out.println(e.getMessage());
            }
            if (status) {
                processInput(Integer.parseInt(option), covidDataList);
            }
        }
    }

    public void showMenu() {
        System.out.println("****************************************");
        System.out.println("1. Get States Name.");
        System.out.println("2. Get District name for given states.");
        System.out.println("3. Display Data by state with in date range.");
        System.out.println("4. Display Confirmed cases by comparing two states for a given date range.");
        System.out.println("5. Exit");
        System.out.print("Please Select Option : ");
    }

    public void processInput(int option, List<CovidData> covidDataList) {
        switch (option) {
            case 1:
                processOption1(covidDataList);
                break;
            case 2:
                ProcessOption2(covidDataList);
                break;
            case 3:
                processOption3(covidDataList);
                break;
            case 4:
                processOption4(covidDataList);
                break;
            case 5:
                System.out.println("Thank You!");
                System.exit(0);
                break;
            default:
                System.out.println("No option found with : " + option);
        }
    }

    public void processOption4(List<CovidData> covidDataList) {
        boolean status = false;
        Date eDate = null;
        Date sDate = null;
        try {
            sDate = covidData.validateStartDate(covidData.getStartDate());
        } catch (InvalidDateException e) {
            System.out.println(e.getMessage());
        }
        if(null != sDate) {
            try {
                eDate = covidData.validateEndDate(covidData.getEndDate());
            } catch (InvalidDateException e) {
                System.out.println(e.getMessage());
            }
        }
        if(null != sDate && null != eDate) {
            try {
                status = covidData.validateStartAndEndDate(sDate, eDate);
            } catch (InvalidDateRangeException e) {
                System.out.println(e.getMessage());
            }
        }

        if(status) {
            String firstStateCode = covidData.getFirstStateCode();
            List<CovidData> firstStateData = covidData.getDataWithinDateRangeAndState(firstStateCode, covidDataList);
            List<CovidData> secondStateData = null;
            String secondStateCode = null;
            try {
                status = covidData.validateFirstStateData(firstStateData);
            } catch (InvalidStateCodeException e) {
                System.out.println(e.getMessage());
                status = false;
            }
            if(status) {
                secondStateCode = covidData.getSecondStateCode();
                secondStateData = covidData.getDataWithinDateRangeAndState(secondStateCode, covidDataList);
                try {
                    status = covidData.validateSecondStateData(secondStateData);
                } catch (InvalidStateCodeException e) {
                    System.out.println(e.getMessage());
                    status = false;
                }
            }

            if(status) {
                Map<String, CovidDataReport> reportMap = covidData.getDataByComparingStateWithinDateRange(firstStateData, secondStateData, sDate, eDate, firstStateCode, secondStateCode);
                try {
                    status = covidData.validateData(reportMap);
                } catch (NoDataFoundException e) {
                    System.out.println(e.getMessage());
                    status = false;
                }
                if (status) {
                    printResults(reportMap);
                }
            }
        }
    }

    public void printResults(Map<String, CovidDataReport> reportMap) {
        System.out.println("DATE        | FIRST STATE | FIRST STATE CONFIRMED TOTAL | SECOND STATE | SECOND STATE CONFIRMED TOTAL");
        reportMap.forEach((key, value) -> System.out.println(key + "  |    " + value.getFState() + "       |          " + value.getFConfirmed() + "              |         " + value.getSState() + "   |    " + value.getSConfirmed()));
    }

    public void processOption3(List<CovidData> covidDataList) {
        Date eDate = null;
        boolean status = false;
        Date sDate = null;
        try {
            sDate = covidData.validateStartDate(covidData.getStartDate());
        } catch (InvalidDateException e) {
            System.out.println(e.getMessage());
        }

        if(null != sDate) {
            try {
                eDate = covidData.validateEndDate(covidData.getEndDate());
            } catch (InvalidDateException e) {
                System.out.println(e.getMessage());
            }
        }

        if(null != sDate && null != eDate) {
            try {
                status = covidData.validateStartAndEndDate(sDate, eDate);
            } catch (InvalidDateRangeException e) {
                System.out.println(e.getMessage());
            }
        }

        if (status) {
            List<CovidData> dataWithinDateRange = covidData.getDataWithinDateRange(sDate, eDate, covidDataList);
            try {
                status = false;
                status = covidData.validateDataForDateRange(dataWithinDateRange);
            } catch (NoDataFoundException e) {
                System.out.println(e.getMessage());
            }
            if(status) {
                Map<String, List<CovidDataReport>> formattedDataForDateRange = covidData.formatDataForDateRange(dataWithinDateRange);
                printResult(formattedDataForDateRange);
            }
        }
    }

    public void printResult(Map<String, List<CovidDataReport>> formattedDataForDateRange) {
        System.out.println("       Date |  State  | Confirmed total");
        formattedDataForDateRange.forEach((date, list) -> list.forEach(report -> System.out.println(date + "  |   " + report.getState() + "    | " + report.getCount())));
    }

    public void ProcessOption2(List<CovidData> covidDataList) {
        List<CovidData> districtByStateList = covidData.getDistrictByState(covidDataList);
        boolean status = false;
        try {
            status = covidData.validateData(districtByStateList);
        } catch (InvalidStateCodeException e) {
            System.out.println(e.getMessage());
        }
        if(status) {
            List<String> districtNames = districtByStateList.stream().map(CovidData::getDistrict).distinct().sorted().collect(Collectors.toList());
            printResult(districtNames);
        }
    }

    public void processOption1(List<CovidData> covidDataList) {
        List<String> stateNames = covidData.getStateName(covidDataList);
        printResult(stateNames);
    }

    public void printResult(List<String> list) {
        list.forEach(System.out::println);
    }
}
