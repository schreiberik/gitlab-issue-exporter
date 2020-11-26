package de.iccas.reportgenerator;

import de.iccas.reportgenerator.gitlab_import.GitlabData;
import de.iccas.reportgenerator.gitlab_import.GitlabImporter;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.Project;
import de.iccas.reportgenerator.word_export.DocumentGenerator;

import java.io.File;
import java.util.List;
import java.util.Map;

//ToDo: Einbeziehung von Issues die während des Zeitraumes geupdated wurden (Commits, Kommentare, etc.)
//ToDo: JUnit Tests???
//ToDo: Code Cleanup (Imports, Methoden, JavaDoc, etc.)

/**
 * Application main class
 */
public class ReportGenerator {

    /**
     * triggers the whole process from importing gitlab data till creating a report document and saving it to the hard drive
     */
    public void generateReport() {

        //GitLab Import
        GitlabImporter gitlabImporter = GitlabImporter.getInstance();
        GitlabData gitlabData = gitlabImporter.importAll();

        //Report Generation
        DocumentGenerator documentGenerator = new DocumentGenerator();
        documentGenerator.generateDocument(gitlabData, "SprintReport.docx");
    }

    /**
     * main method
     *
     * @param args no arguments needed here
     */
    public static void main(String[] args) {
        ReportGenerator reportGenerator = new ReportGenerator();
        reportGenerator.generateReport();
    }
}
