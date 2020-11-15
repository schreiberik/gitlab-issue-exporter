package de.iccas.reportgenerator.gitlab_import;

import org.gitlab4j.api.models.Event;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.Project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitlabData {

    private Map<Project, List<Issue>> issuesByProject;
    private Map<Integer, List<Event>> eventsByProject;

    public GitlabData() {
        this.issuesByProject = new HashMap<>();
        this.eventsByProject = new HashMap<>();
    }

    public GitlabData(Map<Project, List<Issue>> issuesByProject, Map<Integer, List<Event>> eventsByIssueID) {

        this.issuesByProject = issuesByProject;
        this.eventsByProject = eventsByIssueID;
    }

    public Map<Project, List<Issue>> getIssuesByProject() {
        return issuesByProject;
    }

    public void setIssuesByProject(Map<Project, List<Issue>> issuesByProject) {
        this.issuesByProject = issuesByProject;
    }

    public Map<Integer, List<Event>> getEventsByProject() {
        return eventsByProject;
    }

    public void setEventsByProject(Map<Integer, List<Event>> eventsByProject) {
        this.eventsByProject = eventsByProject;
    }
}
