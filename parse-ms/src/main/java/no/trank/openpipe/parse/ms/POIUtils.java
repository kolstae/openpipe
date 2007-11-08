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
package no.trank.openpipe.parse.ms;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.POIDocument;
import org.apache.poi.hpsf.CustomProperties;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Functionality shared by the ms parsers.
 * 
 * @version $Revision$
 */
public class POIUtils {
   /**
    * Removes control codes from the text returned by the poi extractors.
    * Adds one space per chunk with control codes.
    * 
    * @param text text to be cleaned
    * @return clean text
    */
   public static String getCleanText(String text) {
      StringBuilder ret = new StringBuilder();
      
      boolean prevControl = false;
      if(text != null) {
         for(int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if(c < 32) {
               if(!prevControl) {
                  ret.append(' ');
               }
               prevControl = true;
            }
            else {
               ret.append(c);
               prevControl = false;
            }
         }
      }
      
      return ret.toString();
   }
   
   
   /**
    * Fetches the \005SummaryInformation and \005DocumentSummaryInformation streams from the poi
    * file system and exctracts all properties of primitive type, String or Date.
    * 
    * @param fs the poi filesystem 
    * @return the properties
    */
   public static Map<String, String> getProperties(POIFSFileSystem fs) {
      Map<String, String> map = new HashMap<String, String>();
      
      try {
         InputStream stream = fs.createDocumentInputStream(SummaryInformation.DEFAULT_STREAM_NAME);
         addProperties(map, PropertySetFactory.create(stream));
      } catch (Exception e) {
         // ignore
      }

      try {
         InputStream stream = fs.createDocumentInputStream(DocumentSummaryInformation.DEFAULT_STREAM_NAME);
         addProperties(map, PropertySetFactory.create(stream));
      } catch (Exception e) {
         // ignore
      }

      return map;
   }
   
   /**
    * Exctracts all properties of primitive type, String or Date from the document's summary information
    * and document summary information.
    * 
    * @param doc the poi document 
    * @return the properties
    */
   public static Map<String, String> getProperties(POIDocument doc) {
      Map<String, String> map = new HashMap<String, String>();

      addProperties(map, doc.getSummaryInformation());
      addProperties(map, doc.getDocumentSummaryInformation());

      return map;
   }
   
   private static void addProperties(Map<String, String> map, Object ob) {
      try {
         for(PropertyDescriptor pd : Introspector.getBeanInfo(ob.getClass()).getPropertyDescriptors()) {
            try {
               if(pd.getReadMethod() != null && pd.getWriteMethod() != null) {
                  Type type = pd.getWriteMethod().getGenericParameterTypes()[0];
                  Class<?> c = type instanceof Class ? (Class<?>)type : null;
                  if(c != null && (c.isPrimitive() || c == String.class || c == Date.class || c == CustomProperties.class)) {
                     Object value = pd.getReadMethod().invoke(ob, new Object[0]);
                     // TODO: decide what to do with custom properties
                     if(value instanceof CustomProperties) {
                     //   CustomProperties cc = (CustomProperties)value;
                     }
                     else if(value != null) {
                        String valueString = value.toString().trim();
                        if(valueString != null) {
                           map.put(pd.getName(), valueString);
                        }
                     }
                  }
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
      } catch (IntrospectionException e) {
         e.printStackTrace();
      }
   }
}