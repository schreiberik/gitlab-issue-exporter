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
    private SprintReportTemplate sprintReportTemplate;

    protected final ch.qos.logback.classic.Logger logger = (Logger) LoggerFactory.getLogger(this.getClass().getSimpleName());

    public DocumentGenerator(GitlabData gitlabData) throws InvalidFormatException {

        objectFactory = Context.getWmlObjectFactory();
        wordprocessingMLPackage = WordprocessingMLPackage.createPackage();
        sprintReportTemplate = new SprintReportTemplate(wordprocessingMLPackage, gitlabData);
        sprintReportTemplate.generateDocument();
        sprintReportTemplate.generateReportFile("Sprintbericht.docx");
    }

}
