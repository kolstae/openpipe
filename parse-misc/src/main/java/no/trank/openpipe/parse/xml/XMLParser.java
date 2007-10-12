package no.trank.openpipe.parse.xml;

import java.io.IOException;
import java.util.Collections;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;

import no.trank.openpipe.parse.api.ParseData;
import no.trank.openpipe.parse.api.Parser;
import no.trank.openpipe.parse.api.ParserException;
import no.trank.openpipe.parse.api.ParserResult;
import no.trank.openpipe.parse.api.ParserResultImpl;

/**
 * @version $Revision: 874 $
 */
public class XMLParser implements Parser {
   private final XMLInputFactory factory;

   public XMLParser() {
      factory = XMLInputFactory.newInstance();
   }

   public XMLParser(XMLInputFactory factory) {
      this.factory = factory;
   }

   public ParserResult parse(ParseData data) throws IOException, ParserException {
      try {
         String encoding = null;
         final XMLEventReader reader = factory.createXMLEventReader(data.getInputStream());
         final StringBuilder buf = new StringBuilder(Math.max(data.getLength() / 5, 64));
         try {
            while (reader.hasNext()) {
               final XMLEvent event = reader.nextEvent();
               if (event.isStartDocument()) {
                  final StartDocument start = (StartDocument) event;
                  encoding = start.getCharacterEncodingScheme();
               } else if (event.isCharacters()) {
                  final Characters chars = event.asCharacters();
                  if (!chars.isWhiteSpace()) {
                     buf.append(chars.getData().trim()).append(' ');
                  }
               }
            }
         } finally {
            try {
               reader.close();
            } catch (XMLStreamException e) {
               // Ignoring
            }
         }
         final ParserResultImpl result = new ParserResultImpl();
         if (buf.length() > 1) {
            buf.setLength(buf.length() - 1);
         }
         if (data.includeProperties()) {
            result.setProperties(Collections.singletonMap("encoding", encoding));
         }
         result.setText(buf.toString());
         return result;
      } catch (XMLStreamException e) {
         throw new ParserException(e);
      }
   }   
}
