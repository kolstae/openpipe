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
 * @version $Revision: 874 $
 */
public class PowerPointParser implements Parser {

   public ParserResult parse(ParseData data) throws IOException, ParserException {
      final HSLFSlideShow doc = new HSLFSlideShow(data.getInputStream());
      final ParserResultImpl result = new ParserResultImpl();
      result.setTitle(doc.getSummaryInformation().getTitle());
      final PowerPointExtractor extractor = new PowerPointExtractor(doc);
      result.setText(extractor.getText());
      return result;
   }
}