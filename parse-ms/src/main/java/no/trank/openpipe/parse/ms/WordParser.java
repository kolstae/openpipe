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