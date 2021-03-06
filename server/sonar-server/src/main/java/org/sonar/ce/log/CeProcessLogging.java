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
package org.sonar.ce.log;

import org.sonar.process.LogbackHelper;
import org.sonar.process.ProcessId;
import org.sonar.process.Props;
import org.sonar.server.app.ServerProcessLogging;

import static org.sonar.ce.log.CeLogging.MDC_CE_TASK_UUID;
import static org.sonar.process.LogbackHelper.LogDomain;

/**
 * Configure logback for the Compute Engine process. Logs are written to file "ce.log" in SQ's log directory.
 */
public class CeProcessLogging extends ServerProcessLogging {

  public CeProcessLogging() {
    super(ProcessId.COMPUTE_ENGINE, "%X{" + MDC_CE_TASK_UUID + "}");
  }

  @Override
  protected void extendConfiguration(LogbackHelper helper, Props props) {
    helper.configureLoggerLogLevelFromDomain("sql", props, ProcessId.COMPUTE_ENGINE, LogDomain.SQL);
    helper.configureLoggerLogLevelFromDomain("es", props, ProcessId.COMPUTE_ENGINE, LogDomain.ES_CLIENT);
    helper.configureLoggersLogLevelFromDomain(JMX_RMI_LOGGER_NAMES, props, ProcessId.COMPUTE_ENGINE, LogDomain.JMX);
  }
}
