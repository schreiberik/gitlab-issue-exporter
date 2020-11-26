package reportgenerator.gitlab_import;

import ch.qos.logback.classic.Logger;
import reportgenerator.ReportGeneratorConfig;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Discussion;
import org.gitlab4j.api.models.Event;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.Project;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Retrieves data from GitLab by utilizing the GitLabApi.
 * Filters can be set with help of ImportSettings to specify which GitLab Issues are to be retrieved
 */
public class GitlabImporter {

    protected final ch.qos.logback.classic.Logger logger = (Logger) LoggerFactory.getLogger(this.getClass().getSimpleName());

    private static GitlabImporter instance; //singleton instance
    private GitlabData gitlabData;
    private GitLabApi gitLabApi;
    private ImportSettings importSettings;

    private GitlabImporter(String url, String token) {

        gitLabApi = new GitLabApi(url, token); //api to access GitLab data
        importSettings = ImportSettings.getInstance(); //import filter settings
        gitlabData = new GitlabData(); //imported data is stored here

        logger.info("Connected successfully to " + url);
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

    /**
     * Imports all issues and events that have been created, edited or closed within the given time frame specified
     * in the configuration file
     *
     * @return GitlabData object, holding a list of all issues and events
     */
    public GitlabData completeImport() {

        logger.info("Starting import...");

        try {

            for (Integer projectID : ReportGeneratorConfig.getInstance().getProjectIDs()) { //iterate over all projects

                Project project = gitLabApi.getProjectApi().getProject(projectID);

                if (project != null) {

                    //get all issues of the project
                    List<Issue> issueList = getIssuesFromProject(project);
                    gitlabData.getIssuesByProject().put(project, issueList);

                    List<Event> events = new ArrayList<>();

                    //get all events that where newly created within the given time frame
                    List<Event> newEvents = getIssueEventsFromProject(project, Constants.ActionType.CREATED);
                    events.addAll(newEvents);

                    //get all events that where edited within the given time frame
                    List<Event> updateEvents = getIssueEventsFromProject(project, Constants.ActionType.UPDATED);
                    events.addAll(updateEvents);

                    //get all events that where closed within the given time frame
                    List<Event> closedEvents = getIssueEventsFromProject(project, Constants.ActionType.CLOSED);
                    events.addAll(closedEvents);

                    gitlabData.getEventsByProjectId().put(projectID, events);

                } else {
                    logger.error("Could not find any project with id: " + projectID + ". Skipping to next project.");
                    continue;
                }
            }

        } catch (GitLabApiException e) {
            e.printStackTrace();
        }

        return gitlabData;
    }

    /**
     * retrieves all issues that are associated to a specified project and have been updated within the
     * timeframe specified in the configuration file
     *
     * @param project the GitLab project for which all issues shall be imported
     * @return all associated issues as list
     * @throws GitLabApiException
     */
    public List<Issue> getIssuesFromProject(Project project) throws GitLabApiException {

        List<Issue> issueList = gitLabApi.getIssuesApi().getIssues(project.getId(), importSettings.getUpdateFilter());
        logger.info("Retrieved " + issueList.size() + " issues for project " + project.getName());

        return issueList;
    }

    /**
     * retrieves all issues that are associated to a specified project and have been updated within the
     * timeframe specified in the configuration file
     *
     * @param projectID the GitLab project ID for which all issues shall be imported
     * @return all associated issues as list
     * @throws GitLabApiException
     */
    public List<Issue> getIssuesFromProject(int projectID) throws GitLabApiException {

        Project project = gitLabApi.getProjectApi().getProject(projectID);
        if (project == null) {
            logger.error("Could not find project with ID: " + projectID);
            return null;
        }
        return getIssuesFromProject(project);
    }

    /**
     * retrieves all issues that are associated to a specified project and have been updated within the
     * timeframe specified in the configuration file
     *
     * @param projectID the GitLab project ID as String for which all issues shall be imported
     * @return all associated issues as list
     * @throws GitLabApiException
     */
    public List<Issue> getIssuesFromProject(String projectID) throws GitLabApiException {

        return getIssuesFromProject(Integer.valueOf(projectID));
    }

    /**
     * Retrieves all issue event for a specific project and issue type that also occured within the timeframe
     * specified in the configuration file
     *
     * @param project
     * @param actionType
     * @return
     */
    List<Event> getIssueEventsFromProject(Project project, Constants.ActionType actionType) {

        List<Event> projectEvents = new ArrayList<>();

        try {
            projectEvents = gitLabApi.getEventsApi().getProjectEvents(
                    project.getId(),
                    actionType,
                    Constants.TargetType.ISSUE,
                    importSettings.getEndDate(),
                    importSettings.getStartDate(),
                    Constants.SortOrder.ASC
            );

        } catch (GitLabApiException e) {
            e.printStackTrace();
        }

        logger.info("Retrieved " + projectEvents.size() + " events with type \"" + actionType.toValue() + "\" for project " + project.getName());

        return projectEvents;
    }

    /**
     * Retrieves all discussions of an issue
     *
     * @param project the related project
     * @param issueID the related issue
     * @return a list of all related discussions
     */
    public List<Discussion> getDiscussions(Project project, int issueID) {

        List<Discussion> issueDiscussions = new ArrayList<>();

        try {
            issueDiscussions = gitLabApi.getDiscussionsApi().getIssueDiscussions(project.getId(), issueID);
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }

        logger.info("Retrieved " + issueDiscussions.size() + " for project " + project.getName());

        return issueDiscussions;
    }

    /**
     * Checks if the project for a given project id exists in GitLab
     *
     * @param projectID the id of the project
     * @return true if the project exists, false otherwise
     */
    private boolean isProjectValid(int projectID) {
        Project project = null;
        try {
            project = gitLabApi.getProjectApi().getProject(projectID);
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
