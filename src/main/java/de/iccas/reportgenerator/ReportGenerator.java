package de.iccas.reportgenerator;

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

public class ReportGenerator {

    public void generateReport() {
        GitlabImporter gitlabImporter = GitlabImporter.getInstance();
        try {
            gitlabImporter.importAll();
            DocumentGenerator documentGenerator = new DocumentGenerator(gitlabImporter.getGitlabImport());
        } catch (GitLabApiException | InvalidFormatException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ReportGenerator reportGenerator = new ReportGenerator();
        reportGenerator.generateReport();
    }
}
