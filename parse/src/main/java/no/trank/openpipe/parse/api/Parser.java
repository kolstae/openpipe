package no.trank.openpipe.parse.api;

import java.io.IOException;

/**

 * @version $Revision: 874 $
 */
public interface Parser {
   ParserResult parse(ParseData data) throws IOException, ParserException;
}
