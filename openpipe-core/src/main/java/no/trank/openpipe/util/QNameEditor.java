package no.trank.openpipe.util;

import java.beans.PropertyEditorSupport;
import javax.xml.namespace.QName;

/**
 * @author David Smiley - dsmiley@mitre.org
 */
public class QNameEditor extends PropertyEditorSupport {
   @Override
   public void setAsText(String text) throws IllegalArgumentException {
      setValue(QName.valueOf(text));
   }
}
