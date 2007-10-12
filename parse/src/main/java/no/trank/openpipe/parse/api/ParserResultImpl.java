package no.trank.openpipe.parse.api;

import java.util.Collections;
import java.util.Map;

/**

 * @version $Revision: 874 $
 */
public class ParserResultImpl implements ParserResult {
   private String title;
   private String text;
   private Map<String, String> properties = Collections.emptyMap();

   public ParserResultImpl() {
   }

   public ParserResultImpl(String text) {
      this.text = text;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getText() {
      return text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public Map<String, String> getProperties() {
      return properties;
   }

   public void setProperties(Map<String, String> properties) {
      if (properties != null) {
         this.properties = properties;
      }
   }
}
