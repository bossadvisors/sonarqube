/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.core.component;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.database.model.Snapshot;
import org.sonar.api.resources.File;
import org.sonar.api.resources.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class ResourceComponentTest {

  private Resource file;

  @Before
  public void prepare() {
    file = File.create("foo.c").setEffectiveKey("myproject:path/to/foo.c");
    file.setKey("path/to/foo.c");
  }

  @Test
  public void db_ids_should_be_optional() {
    ResourceComponent component = new ResourceComponent(file, new Snapshot());

    assertThat(component.snapshotId()).isNull();
    assertThat(component.resourceId()).isNull();
  }

  @Test
  public void db_ids_should_be_set() {
    Snapshot snapshot = new Snapshot();
    snapshot.setId(123);
    file.setId(456);
    snapshot.setResourceId(456);
    ResourceComponent component = new ResourceComponent(file, snapshot);

    assertThat(component.snapshotId()).isEqualTo(123);
    assertThat(component.resourceId()).isEqualTo(456);
  }

  @Test
  public void should_use_effective_key() {
    ResourceComponent component = new ResourceComponent(file);
    assertThat(component.key()).isEqualTo("myproject:path/to/foo.c");
  }

  @Test
  public void effective_key_should_be_set() {
    try {
      File file = File.create("foo.c");
      new ResourceComponent(file);
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e).hasMessage("Missing component key");
    }
  }
}
