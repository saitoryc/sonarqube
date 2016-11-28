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
package org.sonar.server.authentication.event;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonar.server.exceptions.ServerException;

import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static java.util.Objects.requireNonNull;

/**
 * User needs to be authenticated. HTTP request is generally redirected to login form.
 */
public class AuthenticationException extends ServerException {
  private final AuthenticationEvent.Source source;
  @CheckForNull
  private final String login;

  private AuthenticationException(Builder builder) {
    super(HTTP_UNAUTHORIZED, builder.message);
    this.source = requireNonNull(builder.source, "source can't be null");
    this.login = builder.login;
  }

  public AuthenticationEvent.Source getSource() {
    return source;
  }

  @CheckForNull
  public String getLogin() {
    return login;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    @CheckForNull
    private AuthenticationEvent.Source source;
    @CheckForNull
    private String login;
    @CheckForNull
    private String message;

    private Builder() {
      // use static factory method
    }

    public Builder setSource(AuthenticationEvent.Source source) {
      this.source = source;
      return this;
    }

    public Builder setLogin(@Nullable String login) {
      this.login = login;
      return this;
    }

    public Builder setMessage(String message) {
      this.message = message;
      return this;
    }

    public AuthenticationException build() {
      return new AuthenticationException(this);
    }
  }
}
