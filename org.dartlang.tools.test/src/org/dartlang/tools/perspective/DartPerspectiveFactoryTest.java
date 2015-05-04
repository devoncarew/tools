/*
 * Copyright (c) 2015, the Dart project authors.
 * 
 * Licensed under the Eclipse Public License v1.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.dartlang.tools.perspective;

import org.dartlang.tools.TestRunnable;
import org.dartlang.tools.TestUtils;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DartPerspectiveFactoryTest {
  @BeforeClass
  public static void preFlight() throws Exception {
    TestUtils.preFlight();
  }

  @Test
  public void testCreatePerspective() throws Throwable {
    TestUtils.syncExec(new TestRunnable() {
      @Override
      public void run() throws Throwable {
        IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.showPerspective(
            DartPerspectiveFactory.PERSPECTIVE_ID,
            workbench.getActiveWorkbenchWindow());
        String perspectiveId = workbench.getActiveWorkbenchWindow().getActivePage().getPerspective().getId();
        Assert.assertEquals(DartPerspectiveFactory.PERSPECTIVE_ID, perspectiveId);
      }
    });
  }
}
