package minoe;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/** A utility for making Lucene Documents from a File. */

public class FileDocument {

    private FileDocument() {
    }

  /** Makes a document for a File.
    The document has these fields:
    path--containing the pathname of the file, as a stored, untokenized field.
    modified--containing the last modified date of the file as a field as created by <a href="lucene.document.DateTools.html">DateTools</a>.
    contents--containing the full contents of the file, as a  Reader field;
    tags--tags associate with this document, stored as :: delimited.
   * @param f
   * @param pathToFile
   * @return
   * @throws java.io.FileNotFoundException
   * @throws IOException
   */
  public static Document Document(File f, String pathToFile) throws java.io.FileNotFoundException, IOException {

	 
        // make a new, empty document
        Document doc = new Document();

        // The pathToFile contains the system file separator.
        // We want to replace this with "::" and in SearchFiles replace
        // "::" with the system path separator.
        String separator = "\\" + File.separator;
        String path = pathToFile.replaceAll(separator, "::");

        // Add the path of the file as a field named "path".  Use a field that is
        // indexed (i.e. searchable), but don't tokenize the field into words.
        doc.add(new Field("path", path, Field.Store.YES, Field.Index.NOT_ANALYZED));

        // add the name of the document.
        doc.add(new Field("file name", f.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));

        // Add the contents of the file to a field named "contents".  Specify a Reader,
        // so that the text of the file is tokenized and indexed, but not stored.
        // Note that FileReader expects the file to be in the system's default encoding.
        // If that's not the case searching for special characters will fail.
       // doc.add(new Field("contents", new FileReader(f), Field.TermVector.WITH_POSITIONS_OFFSETS));

        doc.add(new Field("contents", new BufferedReader(new InputStreamReader(new FileInputStream(f),"UTF-8"))));


        // return the document
        return doc;
  }

  
}
    
