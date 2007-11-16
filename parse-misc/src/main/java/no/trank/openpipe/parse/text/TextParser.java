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
package no.trank.openpipe.parse.text;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

import no.trank.openpipe.parse.api.ParseData;
import no.trank.openpipe.parse.api.Parser;
import no.trank.openpipe.parse.api.ParserException;
import no.trank.openpipe.parse.api.ParserResult;
import no.trank.openpipe.parse.api.ParserResultImpl;

/**
 * @version $Revision$
 */
public class TextParser implements Parser, Closeable {
   private final TextDecoder decoder;

   public TextParser() {
      this(new TextDecoder());
   }

   public TextParser(TextDecoder decoder) {
      this.decoder = decoder;
   }

   @Override
   public ParserResult parse(ParseData data) throws IOException, ParserException {
      final String text = decode(data);
      if (text == null) {
         throw new ParserException("Unable to decode data");
      }
      final ParserResultImpl result = new ParserResultImpl(text);
      if (data.includeProperties()) {
         final String lang = decoder.getLanguage();
         if (lang != null) {
            final HashMap<String, String> props = new HashMap<String, String>();
            props.put("encoding", decoder.getEncoding());
            props.put("language", lang);
            result.setProperties(props);
         } else {
            result.setProperties(Collections.singletonMap("encoding", decoder.getEncoding()));
         }
      }
      return result;
   }

   protected String decode(ParseData data) throws IOException {
      return decoder.decode(data);
   }

   @Override
   public void close() throws IOException {
      decoder.close();
   }
}
