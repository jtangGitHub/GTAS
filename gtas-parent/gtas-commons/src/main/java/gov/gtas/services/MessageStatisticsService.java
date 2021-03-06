package gov.gtas.services;

import gov.gtas.model.ApisStatistics;
import gov.gtas.model.DashboardMessageStats;
import gov.gtas.model.PnrStatistics;

public interface MessageStatisticsService {
    
    public PnrStatistics getPnrStatistics();
    public ApisStatistics getApisStatistics();
    public DashboardMessageStats getDashboardAPIMessageStats();
    public DashboardMessageStats getDashboardPNRMessageStats();

}
