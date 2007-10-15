package no.trank.openpipe.parse.api;

import java.io.IOException;

/**

 * @version $Revision$
 */
public interface Parser {
   ParserResult parse(ParseData data) throws IOException, ParserException;
}
