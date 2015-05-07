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
package org.dartlang.tools.utils;

import org.dartlang.tools.TestRunnable;
import org.dartlang.tools.TestUtils;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.progress.IProgressConstants;
import org.junit.Assert;
import org.junit.Test;

public class PlatformUtilsTest {
  @Test
  public void testShowView() throws Throwable {
    TestUtils.syncExec(new TestRunnable() {
      @Override
      public void run() throws Throwable {
        IWorkbenchPage page = PlatformUtils.getActivePage();

        PlatformUtils.showView(IProgressConstants.PROGRESS_VIEW_ID);
        Assert.assertEquals(
            IProgressConstants.PROGRESS_VIEW_ID,
            page.getActivePart().getSite().getId());

        PlatformUtils.showView(IPageLayout.ID_PROBLEM_VIEW);
        Assert.assertEquals(IPageLayout.ID_PROBLEM_VIEW, page.getActivePart().getSite().getId());
      }
    });
  }
}
