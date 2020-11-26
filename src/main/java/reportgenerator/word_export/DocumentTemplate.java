package reportgenerator.word_export;

import ch.qos.logback.classic.Logger;
import reportgenerator.ReportGeneratorConfig;
import reportgenerator.gitlab_import.GitlabData;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ObjectFactory;
import org.gitlab4j.api.models.Assignee;
import org.gitlab4j.api.models.Issue;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic template class for word documents. Extend this class to create your own templates, e.g. SprintReportTemplate
 * This class contains various functions to ease the filtering and formatting of imported data
 */
public abstract class DocumentTemplate {
    protected ObjectFactory objectFactory;
    protected WordprocessingMLPackage wordprocessingMLPackage;
    protected MainDocumentPart mainDocumentPart;
    protected GitlabData gitlabData;
    protected int writableWidthTwips;

    //Logback Logger
    protected final ch.qos.logback.classic.Logger logger = (Logger) LoggerFactory.getLogger(this.getClass().getSimpleName());

    /**
     * Constructor
     *
     * @param wordprocessingMLPackage the docx4j word processing package
     * @param gitlabData the imported GitLab data
     */
    public DocumentTemplate(WordprocessingMLPackage wordprocessingMLPackage, GitlabData gitlabData) {
        this.wordprocessingMLPackage = wordprocessingMLPackage;
        this.gitlabData = gitlabData;
        objectFactory = Context.getWmlObjectFactory();

        //get the page dimensions to set table cell width later on
        writableWidthTwips = wordprocessingMLPackage.getDocumentModel().getSections().get(0).getPageDimensions().getWritableWidthTwips();
    }

    public abstract void generateDocument();

    /**
     * creates or overwrites a file in the root path on the file system. if overwriting, make sure to close the file befor running. otherwise an exception will be thrown
     *
     * @param fileName the name of the file to be created / overwritten
     */
    public void generateReportFile(String fileName) {
        File exportFile = new File(fileName);
        try {
            wordprocessingMLPackage.save(exportFile);
        } catch (Docx4JException e) {
            e.printStackTrace();
        }
    }

    /**
     * transforms a list of string values into a single string containing the separated values
     *
     * @param values    a list holding string values
     * @param separator the string, which will be used to separate the values, e.g. a comma
     * @return a string containing the comma separated values of the passed array
     */
    protected String implode(List<String> values, String separator) {
        StringBuilder commaSeparatedValues = new StringBuilder();

        for (int i = 0; i < values.size(); i++) {
            commaSeparatedValues.append(values.get(i));

            //add comma, if element is not the last element
            if (i != values.size() - 1) {
                commaSeparatedValues.append(separator);
            }
        }

        return commaSeparatedValues.toString();
    }

    /**
     * converts a list of assignees into a single string containing the comma separated names of all assignees in the list
     *
     * @param values a list of assignees
     * @return the string containing the assignees names separated by commas
     */
    protected String printAssignees(List<Assignee> values) {
        StringBuilder commaSeparatedValues = new StringBuilder();

        for (int i = 0; i < values.size(); i++) {
            commaSeparatedValues.append(values.get(i).getName());

            //add comma, if element is not the last element
            if (i != values.size() - 1) {
                commaSeparatedValues.append(",");
            }
        }

        return commaSeparatedValues.toString();
    }

    /**
     * this methods filters the issue's labels to remove all labels that are not associated with the issues progress
     *
     * @param issue the issue
     * @return a filtered list of strings, containing only progress related issues (e.g. in development, testing, closed, etc.) The list of valid labels can be set in the configuration file
     */
    protected List<String> filterProgressLabels(Issue issue) {
        List<String> filteredIssues = new ArrayList<>();

        for (String issueLabel : issue.getLabels()) {
            for (String statusLabel : ReportGeneratorConfig.getInstance().getStatusLabels()) {
                if (issueLabel.equals(statusLabel))
                    filteredIssues.add(issueLabel);
            }
        }

        return filteredIssues;
    }

    /**
     * this methods filters the issue's labels to remove all labels that are not associated with the deliverable type
     *
     * @param issue the issue
     * @return a filtered list of strings, containing only deliverable related issues (e.g. feature, bugfix, etc.) The list of valid labels can be set in the configuration file
     */
    protected List<String> filterDeliverableTypeLabels(Issue issue) {
        List<String> filteredIssues = new ArrayList<>();

        for (String issueLabel : issue.getLabels()) {
            for (String issueTypeLabel : ReportGeneratorConfig.getInstance().getIssueTypeLabels()) {
                if (issueLabel.equals(issueTypeLabel))
                    filteredIssues.add(issueLabel);
            }
        }

        return filteredIssues;
    }

    /**
     * converts and rounds a number of seconds to hours for human readable formats (gitlab time estimation is saved in seconds)
     *
     * @param seconds time estimation in seconds
     * @return conversion of time estimation in hours
     */
    protected int secondsToHours(int seconds) {
        return Math.round((seconds / 60) / 60);
    }

    /**
     * calucates the cell width twips according to the numbers of table columns
     * @param tableColumns the amount of columns in the table
     * @return the cell width twips
     */
    protected int getCellWidthTwipsForTable(int tableColumns)
    {
        int cellWidthTwips = Double.valueOf(Math.floor(writableWidthTwips /  tableColumns)).intValue();
        return cellWidthTwips;
    }
}
