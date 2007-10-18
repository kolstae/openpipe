package no.trank.openpipe.parse.api;

import java.io.IOException;

/**
 * An interface representing a parser.
 * 
 * @version $Revision$
 */
public interface Parser {

   /**
    * Parse the given data.
    * 
    * @param data the data to be parsed.
    * 
    * @return the result of the parse. <b>Note</b> must <i>not</i> return <tt>null</tt>.
    * 
    * @throws IOException if an I/O-error occured.
    * @throws ParserException if the data could not be parsed.
    */
   ParserResult parse(ParseData data) throws IOException, ParserException;
}
