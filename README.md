# gitlab-issue-exporter
A small tool that connects to a GitLab instance and imports mainly issue data and generates a report document (MS Word file) from that data. 
I was looking for a tool to create weekly reports for some GitLab projects, but could not find one that met my requirements. So I made one.

The tool comes with a config file, where you can specify various settings:
- Which projects do you want to include?
- The timeframe for the report
- Relevant GitLab labels
- etc.

It is using the GitLabApi Library for data import and Docx4j for document generation.
Since there might be people with the same problem I had some weeks ago, I decided to share my work here. Feel free to use or edit the program to your needs.
There are still some minor imperfections I noted as ToDo in the ReportGenerator.java (main file)

To start open the config_template.properties and add login data for your repository etc. When finished rename the file to config.properties and start the tool via main-method in ReportGenerator.java
