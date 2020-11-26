package reportgenerator.gitlab_import;

import org.gitlab4j.api.models.Discussion;
import org.gitlab4j.api.models.Event;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.Project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * structure to encapsulate all data that is coming from gitlab
 */
public class GitlabData {

    private Map<Project, List<Issue>> issuesByProject;
    private Map<Integer, List<Event>> eventsByProjectId;
    private Map<Integer, List<Discussion>> discussionsByIssueId;

    /**
     * default constructor
     */
    public GitlabData() {
        this.issuesByProject = new HashMap<>();
        this.eventsByProjectId = new HashMap<>();
        this.discussionsByIssueId = new HashMap<>();
    }

    /**
     * constructor
     * @param issuesByProject all imported issues grouped by project
     * @param eventsByProjectId all imported event grouped by project id
     * @param discussionsByIssueId all imported discussions grouped by issue id
     */
    public GitlabData(Map<Project, List<Issue>> issuesByProject, Map<Integer, List<Event>> eventsByProjectId, Map<Integer, List<Discussion>> discussionsByIssueId) {

        this.issuesByProject = issuesByProject;
        this.eventsByProjectId = eventsByProjectId;
        this.discussionsByIssueId = discussionsByIssueId;
    }

    public Map<Project, List<Issue>> getIssuesByProject() {
        return issuesByProject;
    }

    public void setIssuesByProject(Map<Project, List<Issue>> issuesByProject) {
        this.issuesByProject = issuesByProject;
    }

    public Map<Integer, List<Event>> getEventsByProjectId() {
        return eventsByProjectId;
    }

    public void setEventsByProjectId(Map<Integer, List<Event>> eventsByProjectId) {
        this.eventsByProjectId = eventsByProjectId;
    }

    public Map<Integer, List<Discussion>> getDiscussionsByIssueId() {
        return discussionsByIssueId;
    }

    public void setDiscussionsByIssueId(Map<Integer, List<Discussion>> discussionsByIssueId) {
        this.discussionsByIssueId = discussionsByIssueId;
    }
}
