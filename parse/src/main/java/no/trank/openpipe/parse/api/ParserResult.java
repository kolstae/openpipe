package no.trank.openpipe.parse.api;

import java.util.Map;

/**

 * @version $Revision: 874 $
 */
public interface ParserResult {
   String getTitle();
   String getText();
   Map<String, String> getProperties();
}
