package no.trank.openpipe.parse.ms;

import java.io.IOException;

import org.apache.poi.hslf.HSLFSlideShow;
import org.apache.poi.hslf.extractor.PowerPointExtractor;

import no.trank.openpipe.parse.api.ParseData;
import no.trank.openpipe.parse.api.Parser;
import no.trank.openpipe.parse.api.ParserException;
import no.trank.openpipe.parse.api.ParserResult;
import no.trank.openpipe.parse.api.ParserResultImpl;

/**
 * Parses .ppt files.
 * 
 * @version $Revision$
 */
public class PowerPointParser implements Parser {

   @Override
   public ParserResult parse(ParseData data) throws IOException, ParserException {
      final HSLFSlideShow doc = new HSLFSlideShow(data.getInputStream());
      final ParserResultImpl result = new ParserResultImpl();
      result.setTitle(doc.getSummaryInformation().getTitle());
      final PowerPointExtractor extractor = new PowerPointExtractor(doc);
      result.setText(POIUtils.getCleanText(extractor.getText()));
      if(data.includeProperties()) {
         result.setProperties(POIUtils.getProperties(doc));
      }
      return result;
   }
}