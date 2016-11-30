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
package org.sonar.batch.repository;

import java.util.List;
import javax.annotation.CheckForNull;
import org.picocontainer.injectors.ProviderAdapter;
import org.sonar.api.batch.bootstrap.ProjectKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.api.utils.log.Profiler;
import org.sonar.batch.analysis.AnalysisProperties;
import org.sonar.batch.analysis.DefaultAnalysisMode;
import org.sonar.batch.rule.ModuleQProfiles;
import org.sonarqube.ws.QualityProfiles.SearchWsResponse.QualityProfile;

public class QualityProfileProvider extends ProviderAdapter {
  private static final Logger LOG = Loggers.get(QualityProfileProvider.class);
  private static final String LOG_MSG = "Load quality profiles";
  private ModuleQProfiles profiles = null;

  public ModuleQProfiles provide(ProjectKey projectKey, QualityProfileLoader loader, ProjectRepositories projectRepositories, AnalysisProperties props, DefaultAnalysisMode mode) {
    if (this.profiles == null) {
      List<QualityProfile> profileList;
      Profiler profiler = Profiler.create(LOG).startInfo(LOG_MSG);
      if (!projectRepositories.exists()) {
        profileList = loader.loadDefault(getSonarProfile(props, mode));
      } else {
        profileList = loader.load(projectKey.get(), getSonarProfile(props, mode));
      }
      profiler.stopInfo();
      profiles = new ModuleQProfiles(profileList);
    }

    return profiles;
  }

  @CheckForNull
  private static String getSonarProfile(AnalysisProperties props, DefaultAnalysisMode mode) {
    String profile = null;
    // Changed not to use '!mode.isIssues()' 2016/11/30 saito
    if (props.properties().containsKey(ModuleQProfiles.SONAR_PROFILE_PROP)) {
      profile = props.property(ModuleQProfiles.SONAR_PROFILE_PROP);
      LOG.warn("Ability to set quality profile from command line using '" + ModuleQProfiles.SONAR_PROFILE_PROP
        + "' is deprecated and will be dropped in a future SonarQube version. Please configure quality profile used by your project on SonarQube server.");
    }
    return profile;
  }

}
