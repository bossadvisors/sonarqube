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

package org.sonar.server.test.ws;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sonar.api.test.MutableTestCase;
import org.sonar.api.test.MutableTestPlan;
import org.sonar.api.test.TestCase;
import org.sonar.api.web.UserRole;
import org.sonar.core.component.SnapshotPerspectives;
import org.sonar.server.user.MockUserSession;
import org.sonar.server.ws.WsTester;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestsShowActionTest {

  static final String TEST_PLAN_KEY = "src/test/java/org/foo/BarTest.java";

  @Mock
  MutableTestPlan testPlan;

  WsTester tester;

  @Before
  public void setUp() throws Exception {
    SnapshotPerspectives snapshotPerspectives = mock(SnapshotPerspectives.class);
    when(snapshotPerspectives.as(MutableTestPlan.class, TEST_PLAN_KEY)).thenReturn(testPlan);
    tester = new WsTester(new TestsWs(new TestsShowAction(snapshotPerspectives), mock(TestsTestableAction.class), mock(TestsPlanAction.class)));
  }

  @Test
  public void show() throws Exception {
    MockUserSession.set().addComponentPermission(UserRole.CODEVIEWER, "SonarQube", TEST_PLAN_KEY);

    MutableTestCase testCase1 = testCase("test1", TestCase.Status.OK, 10L, 32);
    MutableTestCase testCase2 = testCase("test2", TestCase.Status.ERROR, 97L, 21);
    when(testPlan.testCases()).thenReturn(newArrayList(testCase1, testCase2));

    WsTester.TestRequest request = tester.newGetRequest("api/tests", "show").setParam("key", TEST_PLAN_KEY);

    request.execute().assertJson("{\n" +
      "  \"tests\": [\n" +
      "    {\n" +
      "      \"name\": \"test1\",\n" +
      "      \"status\": \"OK\",\n" +
      "      \"durationInMs\": 10,\n" +
      "      \"coveredLines\": 32\n" +
      "    },\n" +
      "    {\n" +
      "      \"name\": \"test2\",\n" +
      "      \"status\": \"ERROR\",\n" +
      "      \"durationInMs\": 97,\n" +
      "      \"coveredLines\": 21\n" +
      "    }\n" +
      "  ],\n" +
      "}\n");
  }

  private MutableTestCase testCase(String name, TestCase.Status status, Long durationInMs, int coveredLines) {
    MutableTestCase testCase = mock(MutableTestCase.class);
    when(testCase.name()).thenReturn(name);
    when(testCase.status()).thenReturn(status);
    when(testCase.durationInMs()).thenReturn(durationInMs);
    when(testCase.countCoveredLines()).thenReturn(coveredLines);
    return testCase;
  }

}