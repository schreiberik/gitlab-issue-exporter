package de.iccas.reportgenerator.gitlab_import;

import de.iccas.reportgenerator.ReportGeneratorConfig;
import org.gitlab4j.api.models.IssueFilter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

//ToDo: in andere Klassen auslagern
public class ImportSettings {

    private static ImportSettings instance; //singleton instance

    private SimpleDateFormat df;
    private Date startDate;
    private Date endDate;

    private ImportSettings() {
        try {

            //import entities from midnight to midnight of the specified days
            df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(TimeZone.getDefault());
            startDate = df.parse(ReportGeneratorConfig.getInstance().getImportStartDate());
            endDate = df.parse(ReportGeneratorConfig.getInstance().getImportEndDate());

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * creates an issue filter to filter issues by their creation date
     * @return the issue filter object
     */
    public IssueFilter getCreationFilter() {
        IssueFilter filter = new IssueFilter();

        filter.setCreatedAfter(startDate);
        filter.setCreatedBefore(endDate);

        return filter;
    }

    /**
     * creates an issue filter to filter issues by their update date
     * @return the issue filter object
     */
    public IssueFilter getUpdateFilter() {
        IssueFilter filter = new IssueFilter();
        filter.setUpdatedAfter(startDate);
        filter.setUpdatedBefore(endDate);

        return filter;
    }


    /**
     * provides the singleton instance
     *
     * @return the singleton instance of ImportSettings
     */
    public static ImportSettings getInstance() {
        if (ImportSettings.instance == null) {
            ImportSettings.instance = new ImportSettings();
        }
        return ImportSettings.instance;
    }
}
