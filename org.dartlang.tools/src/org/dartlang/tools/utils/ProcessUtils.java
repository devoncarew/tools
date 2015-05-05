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

import java.io.IOException;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

public class ProcessUtils {
  /**
   * Exec the given command and return the stdout output. Return `null` on any error.
   * 
   * @param cliString
   * @return
   */
  public static String exec(String[] args) {
    try {
      Process process = Runtime.getRuntime().exec(args);
      /*int exitCode =*/process.waitFor();
      String stdout = new String(ByteStreams.toByteArray(process.getInputStream()));
      //String stderr = new String(ByteStreams.toByteArray(process.getErrorStream()));
      Closeables.closeQuietly(process.getInputStream());
      return stdout == null ? null : stdout.trim();
    } catch (IOException e) {
      return null;
    } catch (InterruptedException e) {
      return null;
    }
  }

  private ProcessUtils() {

  }
}
