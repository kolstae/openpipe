package no.trank.openpipe.parse.api;

import java.util.Map;

/**
 * An interface representing a parser-result.
 * 
 * @version $Revision$
 */
public interface ParserResult {

   /**
    * Gets the title of the document, if any.
    * 
    * @return the title of the document.
    */
   String getTitle();

   /**
    * Gets the text of the document.
    * 
    * @return the text of the document.
    */
   String getText();

   /**
    * Gets the properties of the document, if any.
    * 
    * @return the properties of the document. Must <i>not</i> be <tt>null</tt>.
    */
   Map<String, String> getProperties();
}
