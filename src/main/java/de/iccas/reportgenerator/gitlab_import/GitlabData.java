package de.iccas.reportgenerator.gitlab_import;

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
    private Map<Integer, List<Event>> eventsByProject;

    public GitlabData() {
        this.issuesByProject = new HashMap<>();
        this.eventsByProject = new HashMap<>(); //todo
    }

    /**
     *
     * @param issuesByProject the mapping of each project and the according issue list
     * @param eventsByProject //todo
     */
    public GitlabData(Map<Project, List<Issue>> issuesByProject, Map<Integer, List<Event>> eventsByProject) {

        this.issuesByProject = issuesByProject;
        this.eventsByProject = eventsByProject;
    }

    /**
     *
     * @return
     */
    public Map<Project, List<Issue>> getIssuesByProject() {
        return issuesByProject;
    }

    public void setIssuesByProject(Map<Project, List<Issue>> issuesByProject) {
        this.issuesByProject = issuesByProject;
    }

    /**
     *
     * @return
     */
    public Map<Integer, List<Event>> getEventsByProject() {
        return eventsByProject;
    }

    /**
     *
     * @param eventsByProject
//     */
    public void setEventsByProject(Map<Integer, List<Event>> eventsByProject) {
        this.eventsByProject = eventsByProject;
    }
}
