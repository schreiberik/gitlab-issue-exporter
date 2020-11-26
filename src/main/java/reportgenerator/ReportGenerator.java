package reportgenerator;

import reportgenerator.gitlab_import.GitlabData;
import reportgenerator.gitlab_import.GitlabImporter;
import reportgenerator.word_export.DocumentGenerator;

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
        GitlabData gitlabData = gitlabImporter.completeImport();

        //Report Generation
        DocumentGenerator documentGenerator = new DocumentGenerator();
        String fileName = ReportGeneratorConfig.getInstance().getDocumentFileName();
        documentGenerator.generateDocument(gitlabData, fileName);
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
