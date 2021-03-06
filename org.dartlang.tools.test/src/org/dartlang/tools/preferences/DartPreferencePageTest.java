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
package org.dartlang.tools.preferences;

import java.io.InputStream;
import java.net.URL;

import org.dartlang.tools.TestRunnable;
import org.dartlang.tools.TestUtils;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.hamcrest.core.StringContains;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

public class DartPreferencePageTest {
  @BeforeClass
  public static void preFlight() throws Exception {
    TestUtils.preFlight();
  }

  private PreferenceDialog dialog;

  @After
  public void after() throws Exception {
    if (dialog != null) {
      Display.getDefault().syncExec(new Runnable() {
        @Override
        public void run() {
          dialog.close();
        }
      });
      dialog = null;
    }
  }

  @Test
  public void testCreatePrefPage() throws Throwable {
    TestUtils.syncExec(new TestRunnable() {
      @Override
      public void run() throws Throwable {
        dialog = PreferencesUtil.createPreferenceDialogOn(
            null,
            DartPreferencePage.PAGE_ID,
            null,
            null);
        dialog.setBlockOnOpen(false);
        dialog.open();

        IPreferencePage page = (IPreferencePage) dialog.getSelectedPage();
        Assert.assertEquals("Dart", page.getTitle());
      }
    });
  }

  @Test
  public void testVerifyDownloadsUrl() throws Throwable {
    URL url = new URL(DartPreferencePage.DOWNLOADS_URL);
    InputStream in = url.openStream();
    String str = new String(ByteStreams.toByteArray(in), Charsets.UTF_8);
    Closeables.closeQuietly(in);
    Assert.assertNotNull(str);

    // Test that we can see the page.
    str = str.toLowerCase();
    Assert.assertThat(str, StringContains.containsString("dart"));
    Assert.assertThat(str, StringContains.containsString("download"));
  }
}
