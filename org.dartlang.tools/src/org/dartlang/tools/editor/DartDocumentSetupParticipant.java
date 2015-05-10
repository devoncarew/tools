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
package org.dartlang.tools.editor;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;

public class DartDocumentSetupParticipant implements IDocumentSetupParticipant {
  @Override
  public void setup(IDocument document) {
    if (document instanceof IDocumentExtension3) {
      IDocumentExtension3 extension3 = (IDocumentExtension3) document;
      IDocumentPartitioner partitioner = new FastPartitioner(
          DartPartitionScanner.getPartitionScanner(),
          DartPartitionScanner.DART_PARTITION_TYPES);
      extension3.setDocumentPartitioner(DartPartitionScanner.DART_PARTITIONING, partitioner);
      partitioner.connect(document);
    }
  }
}
