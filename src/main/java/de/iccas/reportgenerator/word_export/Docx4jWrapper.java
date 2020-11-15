package de.iccas.reportgenerator.word_export;

import org.docx4j.jaxb.Context;
import org.docx4j.wml.*;

/**
 * This class wraps various functionalities of docx4j to provide a more convenient way for structuring and styling documents
 */
public class Docx4jWrapper {

    private static ObjectFactory objectFactory = Context.getWmlObjectFactory();

    public static R generateRun(String string) {

        if (string == null || string.isEmpty() || string.equals("null"))
            string = "";

        Text text = objectFactory.createText();
        text.setValue(string);

        R run = objectFactory.createR();
        run.getContent().add(text);
        return run;
    }

    public static R generateRun(String[] strings) {

        R run = objectFactory.createR();

        for (String string : strings) {
            Text text = objectFactory.createText();
            text.setValue(string);
            run.getContent().add(text);
        }

        return run;
    }

    public static R generateStyledRun(String string, RPr rpr) {
        R run = generateRun(string);
        run.setRPr(rpr);

        return run;
    }

    public static R generateStyledRun(String[] strings, RPr rpr) {
        R run = generateRun(strings);
        run.setRPr(rpr);

        return run;
    }

    /**
     * creates an RPr object for bold text style
     *
     * @return the RPr set for bold text style
     */
    public static RPr getBoldStyle() {
        RPr rpr = objectFactory.createRPr();
        BooleanDefaultTrue b = new BooleanDefaultTrue();
        rpr.setB(b);

        return rpr;
    }

    /**
     * creates an RPr object for italic text style
     *
     * @return the RPr set for italic text style
     */
    public static RPr getItalicRpr() {
        RPr rpr = objectFactory.createRPr();
        BooleanDefaultTrue b = new BooleanDefaultTrue();
        rpr.setI(b);

        return rpr;
    }
}
