/*
 * SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.scanner.scan.report;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.ScannerSide;
import org.sonar.api.batch.rule.Rule;
import org.sonar.api.batch.rule.Rules;
import org.sonar.api.resources.Project;
import org.sonar.api.rules.RulePriority;
import org.sonar.scanner.DefaultProjectTree;
import org.sonar.scanner.index.BatchComponent;
import org.sonar.scanner.index.BatchComponentCache;
import org.sonar.scanner.issue.IssueCache;
import org.sonar.scanner.issue.tracking.TrackedIssue;
import org.sonar.scanner.scan.filesystem.InputPathCache;

@ScannerSide
public class IssuesReportBuilder {

  private static final Logger LOG = LoggerFactory.getLogger(IssuesReportBuilder.class);

  private final IssueCache issueCache;
  private final Rules rules;
  private final BatchComponentCache resourceCache;
  private final DefaultProjectTree projectTree;
  private final InputPathCache inputPathCache;

  public IssuesReportBuilder(IssueCache issueCache, Rules rules, BatchComponentCache resourceCache, DefaultProjectTree projectTree, InputPathCache inputPathCache) {
    this.issueCache = issueCache;
    this.rules = rules;
    this.resourceCache = resourceCache;
    this.projectTree = projectTree;
    this.inputPathCache = inputPathCache;
  }

  public IssuesReport buildReport() {
    Project project = projectTree.getRootProject();
    IssuesReport issuesReport = new IssuesReport();
    issuesReport.setNoFile(!inputPathCache.allFiles().iterator().hasNext());
    issuesReport.setTitle(project.getName());
    issuesReport.setDate(project.getAnalysisDate());

    processIssues(issuesReport, issueCache.all());

    return issuesReport;
  }

  private void processIssues(IssuesReport issuesReport, Iterable<TrackedIssue> issues) {
    for (TrackedIssue issue : issues) {
      Rule rule = findRule(issue);
      RulePriority severity = RulePriority.valueOf(issue.severity());
      BatchComponent resource = resourceCache.get(issue.componentKey());
      if (!validate(issue, rule, resource)) {
        continue;
      }
      if (issue.resolution() != null) {
        issuesReport.addResolvedIssueOnResource(resource, rule, severity);
      } else {
        issuesReport.addIssueOnResource(resource, issue, rule, severity);
      }
    }
  }

  private static boolean validate(TrackedIssue issue, @Nullable Rule rule, @Nullable BatchComponent resource) {
    if (rule == null) {
      LOG.warn("Unknow rule for issue {}", issue);
      return false;
    }
    if (resource == null) {
      LOG.debug("Unknow resource with key {}", issue.componentKey());
      return false;
    }
    return true;
  }

  @CheckForNull
  private Rule findRule(TrackedIssue issue) {
    return rules.find(issue.getRuleKey());
  }

}
