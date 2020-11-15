package de.iccas.reportgenerator.gitlab_import;

import ch.qos.logback.classic.Logger;
import de.iccas.reportgenerator.ReportGeneratorConfig;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Discussion;
import org.gitlab4j.api.models.Event;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.Project;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GitlabImporter {

    protected final ch.qos.logback.classic.Logger logger = (Logger) LoggerFactory.getLogger(this.getClass().getSimpleName());

    private static GitlabImporter instance; //singleton instance
    private GitlabData gitlabData;
    private GitLabApi gitLabConnection;
    private ImportSettings importSettings;

    private GitlabImporter(String url, String token) {

        importSettings = ImportSettings.getInstance();
        gitLabConnection = new GitLabApi(url, token);
        logger.info("Connected successfully to " + url);

        gitlabData = new GitlabData();
    }

    /**
     * provides the singleton instance
     *
     * @return the singleton instance of GitlabImporter
     */
    public static GitlabImporter getInstance() {
        if (GitlabImporter.instance == null) {
            String repositoryURL = ReportGeneratorConfig.getInstance().getRepository();
            String accessToken = ReportGeneratorConfig.getInstance().getAccessToken();
            GitlabImporter.instance = new GitlabImporter(repositoryURL, accessToken);
        }
        return GitlabImporter.instance;
    }

    public void importAll() throws GitLabApiException {

        logger.info("Starting import...");



        for (Integer projectID : ReportGeneratorConfig.getInstance().getProjectIDs()) {

            Project project = gitLabConnection.getProjectApi().getProject(projectID);

            if (project != null) {
                List<Issue> issueList = getIssuesFromProject(project);
                gitlabData.getIssuesByProject().put(project, issueList);

                List<Event> events = new ArrayList<>();

                List<Event> newEvents = getIssueEventsFromProject(project, Constants.ActionType.CREATED);
                events.addAll(newEvents);

                List<Event> updateEvents = getIssueEventsFromProject(project, Constants.ActionType.UPDATED);
                events.addAll(updateEvents);

                List<Event> closeEvents = getIssueEventsFromProject(project, Constants.ActionType.CLOSED);
                events.addAll(closeEvents);

                gitlabData.getEventsByProject().put(projectID, events);

            } else {
                logger.error("Could not find any project with id: " + projectID + ". Skipping to next project.");
                continue;
            }
        }
    }

    public List<Issue> getIssuesFromProject(Project project) throws GitLabApiException {

        List<Issue> issueList = gitLabConnection.getIssuesApi().getIssues(project.getId(), importSettings.getUpdateFilter());
        logger.info("Retrieved " + issueList.size() + " issues for project " + project.getName());

        return issueList;
    }

    public List<Issue> getIssuesFromProject(int projectID) throws GitLabApiException {

        Project project = gitLabConnection.getProjectApi().getProject(projectID);
        if (project == null) {
            logger.error("Could not find project with ID: " + projectID);
            return null;
        }
        return getIssuesFromProject(project);
    }

    public List<Issue> getIssuesFromProject(String projectID) throws GitLabApiException {

        return getIssuesFromProject(Integer.valueOf(projectID));
    }
    //Events //Todo
    List<Event> getIssueEventsFromProject(Project project, Constants.ActionType actionType) {

        List<Event> projectEvents = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getDefault());
        try {
            Date startDate = df.parse(ReportGeneratorConfig.getInstance().getImportStartDate());
            Date endDate = df.parse(ReportGeneratorConfig.getInstance().getImportEndDate());
            projectEvents = gitLabConnection.getEventsApi().getProjectEvents(project.getId(), actionType, Constants.TargetType.ISSUE, endDate, startDate, Constants.SortOrder.ASC);

        } catch (ParseException | GitLabApiException e) {
            e.printStackTrace();
        }

        logger.info("Retrieved " + projectEvents.size() + " events with type \"" + actionType.toValue() + "\" for project " + project.getName());
        return projectEvents;
    }

    //Discussion
    public List<Discussion> getDiscussions(Project project, int issueID)
    {
        List<Discussion> issueDiscussions = new ArrayList<>();
        try {
            issueDiscussions = gitLabConnection.getDiscussionsApi().getIssueDiscussions(project.getId(), issueID);
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }

        logger.info("Retrieved " + issueDiscussions.size() + " for project " + project.getName());
        return issueDiscussions;
    }

    private boolean getProject(int projectID)
    {
        Project project = null;
        try {
            project = gitLabConnection.getProjectApi().getProject(projectID);
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }
        if (project == null) {
            logger.error("Could not find project with ID: " + projectID);
            return false;
        }
        return true;
    }


    public static GitLabApi getGitLabConnection() {
        return GitlabImporter.getGitLabConnection();
    }

    public GitlabData getGitlabImport() {
        return gitlabData;
    }
}
