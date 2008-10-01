/*
 * Copyright 2007  T-Rank AS
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.trank.openpipe.step.xml;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import javax.xml.stream.XMLStreamException;

/**
 * @version $Revision$
 */
public interface DocumentWriter extends Closeable {
   /**
    * Resets the writer to the given file.
    * <br/>
    * <em>Note</em> no cleanup is required here. {@link #close()} and {@link #endDocument()} should already be called.
    *
    * @param file the file the start writing to.
    *
    * @throws FileNotFoundException if the file can not be opened for writing.
    * @throws XMLStreamException if an XML-error occures.
    */
   void reset(File file) throws FileNotFoundException, XMLStreamException;

   /**
    * Starts a document with the given root element.
    *
    * @param rootElementName the root element.
    *
    * @throws XMLStreamException if an XML-error occures.
    */
   void startDocument(String rootElementName) throws XMLStreamException;

   /**
    * Ends the document and the root element.
    *
    * @throws XMLStreamException if an XML-error occures.
    */
   void endDocument() throws XMLStreamException;

   /**
    * Starts a new element.
    *
    * @param elementName the name of the new element.
    *
    * @throws XMLStreamException if an XML-error occures.
    */
   void startElement(String elementName) throws XMLStreamException;

   /**
    * Adds an attribute to the current element.
    * <br/>
    * How multiple values are handled, is up to the implementation.
    *
    * @param name the name of the attribute.
    * @param values the values to add as attribute.
    *
    * @throws XMLStreamException if an XML-error occures.
    */
   void addAttribute(String name, List<String> values) throws XMLStreamException;

   /**
    * Adds an element to the current element.
    * <br/>
    * How multiple values are handled, is up to the implementation.
    *
    * @param name the name of the element.
    * @param values the values to add to the elements.
    *
    * @throws XMLStreamException if an XML-error occures.
    */
   void addElement(String name, List<String> values) throws XMLStreamException;

   /**
    * Ends the current element.
    *
    * @throws XMLStreamException if an XML-error occures.
    */
   void endElement() throws XMLStreamException;
}
