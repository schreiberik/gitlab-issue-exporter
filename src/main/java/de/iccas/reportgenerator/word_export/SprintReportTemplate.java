package de.iccas.reportgenerator.word_export;

import de.iccas.reportgenerator.gitlab_import.GitlabData;
import org.docx4j.jaxb.Context;
import org.docx4j.model.table.TblFactory;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.Project;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * Template for the sprint review document
 */
public class SprintReportTemplate extends DocumentTemplate {

    public SprintReportTemplate(WordprocessingMLPackage wordprocessingMLPackage, GitlabData gitlabData) {
        super(wordprocessingMLPackage, gitlabData);
    }

    @Override
    public void generateDocument() {

        this.mainDocumentPart = wordprocessingMLPackage.getMainDocumentPart();

        //Title
        mainDocumentPart.addStyledParagraphOfText("Title", "Sprint Report");

        //Issues
        Map<Project, List<Issue>> issuesByProject = gitlabData.getIssuesByProject();
        for (Project key : issuesByProject.keySet()) {

            //skip projects without any issues in the defined time frame
            if (issuesByProject.get(key).isEmpty())
                continue;

            //Project title caption
            Text projectTitleText = objectFactory.createText();
            projectTitleText.setValue("Projekt " + key.getName() + ":");

            //Project title font size
            RPr rpr = objectFactory.createRPr();
            BooleanDefaultTrue b = new BooleanDefaultTrue();
            rpr.setB(b);
            HpsMeasure s26pt = Context.getWmlObjectFactory().createHpsMeasure();
            s26pt.setVal(BigInteger.valueOf(26));
            rpr.setSz(s26pt);
            R projectTitleRun = objectFactory.createR();
            projectTitleRun.getContent().add(projectTitleText);
            projectTitleRun.setRPr(rpr);
            P projectTitleParagraph = objectFactory.createP();
            projectTitleParagraph.getContent().add(projectTitleRun);

            mainDocumentPart.getContent().add(projectTitleParagraph);

            for (Issue issue : issuesByProject.get(key)) {

                mainDocumentPart.addObject(generateIssueMetaInfTable(issue));
                mainDocumentPart.addObject(generateIssueDescriptionTable(issue));
                mainDocumentPart.addObject(generateIssueProgressTable(issue));
                mainDocumentPart.addObject(generateIssueDeliverableTable(issue));
                mainDocumentPart.addParagraphOfText(""); //empty paragraph to separeate the tables from each other
            }
        }
    }

    /**
     * generates a table with meta information about the issue (goal, assignee, time estimated and time spent)
     *
     * @param issue the gitlab issue
     * @return the accordingly build docx4j table, holding the data from the passed gitlab issue
     */
    public Tbl generateIssueMetaInfTable(Issue issue) {

        int rows = 2;
        int columns = 3;

        int cellWidthTwips = new Double(Math.floor(writableWidthTwips / columns)).intValue();
        Tbl table = TblFactory.createTable(rows, columns, cellWidthTwips);
        logger.debug("Creating table. Rows: " + rows + " Columns: " + columns + " Cell Width Twips: " + cellWidthTwips);

        //get table header structure
        Tr headerRow = (Tr) table.getContent().get(0);
        Tc headerRowCol1 = (Tc) headerRow.getContent().get(0);
        P headerRowCol1Para1 = (P) headerRowCol1.getContent().get(0);
        Tc headerRowCol2 = (Tc) headerRow.getContent().get(1);
        P headerRowCol2Para1 = (P) headerRowCol2.getContent().get(0);
        Tc headerRowCol3 = (Tc) headerRow.getContent().get(2);
        P headerRowCol3Para1 = (P) headerRowCol3.getContent().get(0);

        //get 1st table row structure
        Tr row1 = (Tr) table.getContent().get(1);
        Tc row1Col1 = (Tc) row1.getContent().get(0);
        P row1Col1Para1 = (P) row1Col1.getContent().get(0);
        Tc row1Col2 = (Tc) row1.getContent().get(1);
        P row1Col2Para1 = (P) row1Col2.getContent().get(0);
        Tc row1Col3 = (Tc) row1.getContent().get(2);
        P row1Col3Para1 = (P) row1Col3.getContent().get(0);

        //Goal / Issue Name
        R goalNameLabel = Docx4jWrapper.generateStyledRun("Ziel", Docx4jWrapper.getBoldStyle());
        headerRowCol1Para1.getContent().add(goalNameLabel);
        R goalNameValue = Docx4jWrapper.generateRun("Issue #" + issue.getIid() + ": " + issue.getTitle());
        row1Col1Para1.getContent().add(goalNameValue);

        //Assignee
        R assigneeLabel = Docx4jWrapper.generateStyledRun("Zugewiesen", Docx4jWrapper.getBoldStyle());
        headerRowCol2Para1.getContent().add(assigneeLabel);
        R assigneeValue = Docx4jWrapper.generateRun(printAssignees(issue.getAssignees()));
        row1Col2Para1.getContent().add(assigneeValue);

        //Time Spent / Estimated
        R timePlanningLabel = Docx4jWrapper.generateStyledRun("Aufwand[h] Ist/Geplant", Docx4jWrapper.getBoldStyle());
        headerRowCol3Para1.getContent().add(timePlanningLabel);
        R timePlanningValue = Docx4jWrapper.generateRun(secondsToHours(issue.getTimeStats().getTotalTimeSpent()) + "/" + secondsToHours(issue.getTimeStats().getTimeEstimate()));
        row1Col3Para1.getContent().add(timePlanningValue);

        return table;
    }

    /**
     * generates a table containing the issues description
     *
     * @param issue the gitlab issue
     * @return the accordingly build docx4j table, holding the data from the passed gitlab issue
     */
    public Tbl generateIssueDescriptionTable(Issue issue) {
        int rows = 2;
        int columns = 1;

        int cellWidthTwips = new Double(Math.floor(writableWidthTwips / columns)).intValue();
        Tbl table = TblFactory.createTable(rows, columns, cellWidthTwips);

        //get table header structure
        Tr headerRow = (Tr) table.getContent().get(0);
        Tc headerRowCol1 = (Tc) headerRow.getContent().get(0);
        P headerRowCol1Para1 = (P) headerRowCol1.getContent().get(0);
        Tr row1 = (Tr) table.getContent().get(1);
        Tc row1Col1 = (Tc) row1.getContent().get(0);
        P row1Col1Para1 = (P) row1Col1.getContent().get(0);

        // Issue description / details
        R descriptionLabel = Docx4jWrapper.generateStyledRun("Durchzuführende Tätigkeiten", Docx4jWrapper.getBoldStyle());
        headerRowCol1Para1.getContent().add(descriptionLabel);
        R descriptionValue = Docx4jWrapper.generateRun(issue.getDescription());
        row1Col1Para1.getContent().add(descriptionValue);

        return table;
    }

    /**
     * generates a table describing the issues progress. issue labels will be filterted according to the config,
     * to remove non progress related issue labels. also labels will be translated
     *
     * @param issue the gitlab issue
     * @return the accordingly build docx4j table, holding the data from the passed gitlab issue
     */
    public Tbl generateIssueProgressTable(Issue issue) {
        int rows = 1;
        int columns = 2;

        //Filter labels that are not used to describe progress and translate progress related labels
        List<String> progressLabelList = filterProgressLabels(issue);
        String status = "";
        if (issue.getState().toString().equals("closed")) {
            status = "Abgeschlossen"; //Caption for closed issues
        } else if (issue.getState().toString().equals("opened") && progressLabelList.isEmpty()) {
            status = "Offen"; //Caption for open issues
        } else {
            status = implode(progressLabelList, ", ");
        }

        int cellWidthTwips = new Double(Math.floor(writableWidthTwips / columns)).intValue();
        Tbl table = TblFactory.createTable(rows, columns, cellWidthTwips);

        //get table header structure
        Tr headerRow = (Tr) table.getContent().get(0);
        Tc headerRowCol1 = (Tc) headerRow.getContent().get(0);
        P headerRowCol1Para1 = (P) headerRowCol1.getContent().get(0);
        Tc headerRowCol2 = (Tc) headerRow.getContent().get(1);
        P headerRowCol2Para1 = (P) headerRowCol2.getContent().get(0);

        //Issue Progress
        R progressLabel = Docx4jWrapper.generateStyledRun("Fortschritt/Bem.", Docx4jWrapper.getBoldStyle());
        headerRowCol1Para1.getContent().add(progressLabel);
        R progressValue = Docx4jWrapper.generateRun(status);
        headerRowCol2Para1.getContent().add(progressValue);

        return table;
    }

    /**
     * generates a table describing in which deliverable type the issue's completion will result
     *
     * @param issue the gitlab issue
     * @return the accordingly build docx4j table, holding the data from the passed gitlab issue
     */

    public Tbl generateIssueDeliverableTable(Issue issue) {
        int rows = 2;
        int columns = 2;

        //Filter labels that are not used to describe the deliverable type and mark issues without according labels
        List<String> deliveryTypeLabelList = filterDeliverableTypeLabels(issue);
        String deliveryType = "";
        if (deliveryTypeLabelList.isEmpty()) {
            deliveryType = "Nicht definiert!"; //caption for issues without proper type label
        } else {
            deliveryType = implode(deliveryTypeLabelList, ", ");
        }

        int cellWidthTwips = new Double(Math.floor(writableWidthTwips / columns)).intValue();
        Tbl table = TblFactory.createTable(rows, columns, cellWidthTwips);

        //get table header structure
        Tr headerRow = (Tr) table.getContent().get(0);
        Tc headerRowCol1 = (Tc) headerRow.getContent().get(0);
        P headerRowCol1Para1 = (P) headerRowCol1.getContent().get(0);
        Tc headerRowCol2 = (Tc) headerRow.getContent().get(1);
        P headerRowCol2Para1 = (P) headerRowCol2.getContent().get(0);
        Tr row1 = (Tr) table.getContent().get(1);
        Tc row1Col1 = (Tc) row1.getContent().get(0);
        P row1Col1Para1 = (P) row1Col1.getContent().get(0);
        Tc row1Col2 = (Tc) row1.getContent().get(1);
        P row1Col2Para1 = (P) row1Col2.getContent().get(0);

        //Deliverable Name
        R deliverableNameLabel = Docx4jWrapper.generateStyledRun("Deliverable", Docx4jWrapper.getBoldStyle());
        headerRowCol1Para1.getContent().add(deliverableNameLabel);
        R deliverableNameValue = Docx4jWrapper.generateRun("Issue #" + issue.getIid() + " " + issue.getTitle());
        row1Col1Para1.getContent().add(deliverableNameValue);

        //Deliverable Type
        R deliverableTypeLabel = Docx4jWrapper.generateStyledRun("Typ", Docx4jWrapper.getBoldStyle());
        headerRowCol2Para1.getContent().add(deliverableTypeLabel);
        R deliverableTypeValue = Docx4jWrapper.generateRun(deliveryType);
        row1Col2Para1.getContent().add(deliverableTypeValue);

        return table;
    }
}


