package de.iccas.reportgenerator;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * this class provides functions to access the settings made in the config.properties
 */
public class ReportGeneratorConfig {

    private static ReportGeneratorConfig instance; //singleton instance
    Configuration config;

    public ReportGeneratorConfig(String configFileName) {

        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setFileName(configFileName)
                                .setListDelimiterHandler(new DefaultListDelimiterHandler(','))); //set default delimiter to comma

        try {
            config = builder.getConfiguration();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * provides the singleton instance
     *
     * @return the singleton instance of GitlabImporter
     */
    public static ReportGeneratorConfig getInstance() {
        if (ReportGeneratorConfig.instance == null) {
            ReportGeneratorConfig.instance = new ReportGeneratorConfig("config.properties");
        }
        return ReportGeneratorConfig.instance;
    }

    /**
     * returns the value of a specific config parameter
     *
     * @param key the config parameter, e.g. gitlab.repository
     * @return the value of the specified config parameter
     */
    public String getConfig(String key) {
        return config.getString(key);
    }

    public String getRepository() {
        return config.getString("gitlab.repository");
    }

    public String getAccessToken() {
        return config.getString("gitlab.accessToken");
    }

    public String getImportStartDate() {
        return config.getString("import.startDay") + " 00:00:00";
    }

    public String getImportEndDate() {
        return config.getString("import.endDay") + " 23:59:59";
    }

    public String getDocumentFileName() {
        return config.getString("document.fileName");
    }

    public int[] getProjectIDs() {
        String[] projectIdStr = config.getStringArray("gitlab.projectIDs");
        int[] projectIDs = new int[projectIdStr.length];

        for (int i = 0; i < projectIdStr.length; i++) {
            projectIDs[i] = Integer.valueOf(projectIdStr[i]);
        }

        return projectIDs;
    }

    public String[] getStatusLabels() {
        return config.getStringArray("issue.statusLabels");
    }

    public String[] getIssueTypeLabels() {
        return config.getStringArray("issue.typeLabels");
    }
}
