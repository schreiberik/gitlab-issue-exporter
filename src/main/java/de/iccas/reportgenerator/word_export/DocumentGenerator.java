package de.iccas.reportgenerator.word_export;

import ch.qos.logback.classic.Logger;
import de.iccas.reportgenerator.gitlab_import.GitlabData;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;
import org.slf4j.LoggerFactory;

public class DocumentGenerator {

    private ObjectFactory objectFactory;
    private WordprocessingMLPackage wordprocessingMLPackage;
    private DocumentTemplate sprintReportTemplate;

    protected final ch.qos.logback.classic.Logger logger = (Logger) LoggerFactory.getLogger(this.getClass().getSimpleName());

    /**
     * getSimpleName
     * generates a report document from the data imported from GitLab
     */
    public DocumentGenerator() {

        //prepare docx4j
        try {
            objectFactory = Context.getWmlObjectFactory();
            wordprocessingMLPackage = WordprocessingMLPackage.createPackage();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param gitlabData the imported data from GitLab
     * @param fileName   the name of the file to be generated
     */
    public void generateDocument(GitlabData gitlabData, String fileName) {
        //generate the actual report document from the template
        sprintReportTemplate = new SprintReportTemplate(wordprocessingMLPackage, gitlabData);
        sprintReportTemplate.generateDocument();
        sprintReportTemplate.generateReportFile(fileName);
    }
}
