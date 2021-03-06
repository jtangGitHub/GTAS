package gov.gtas.services;

import gov.gtas.model.YTDAirportStatistics;
import gov.gtas.model.YTDRules;

import java.util.List;

public interface YTDStatisticsService {

    public List<YTDRules> getYTDRules();
    public List<YTDAirportStatistics> getYTDAirportStats();
}
