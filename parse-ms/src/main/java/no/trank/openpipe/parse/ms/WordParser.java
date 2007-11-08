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

import java.io.IOException;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;

import no.trank.openpipe.parse.api.ParseData;
import no.trank.openpipe.parse.api.Parser;
import no.trank.openpipe.parse.api.ParserException;
import no.trank.openpipe.parse.api.ParserResult;
import no.trank.openpipe.parse.api.ParserResultImpl;

/**
 * Parses .doc files.
 * 
 * @version $Revision$
 */
public class WordParser implements Parser {

   @Override
   public ParserResult parse(ParseData data) throws IOException, ParserException {
      final HWPFDocument doc = new HWPFDocument(data.getInputStream());
      final ParserResultImpl result = new ParserResultImpl();
      result.setTitle(doc.getSummaryInformation().getTitle());
      final WordExtractor extractor = new WordExtractor(doc);
      result.setText(POIUtils.getCleanText(extractor.getText()));
      if(data.includeProperties()) {
         result.setProperties(POIUtils.getProperties(doc));
      }
      return result;
   }
}