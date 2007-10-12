package no.trank.openpipe.api.document;

import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * This class represents a field-value with annotations (if any).
 * 
 * @version $Revision:712 $
 */
public interface AnnotatedField {
   /**
    * Gets the value of the field.
    * 
    * @return the value of the field.
    */
   String getValue();

   /**
    * Adds a list of annotations for this field-value.
    * 
    * @param type the type of annotations.
    * @param annotations the list of annotations.
    * 
    * @return <tt>true</tt> if this type of annotations was <i>not</i> allready added, else <tt>false</tt>.
    */
   boolean add(String type, List<? extends Annotation> annotations);

   /**
    * Creates an iterator for a certain type of annotations.
    * 
    * @param type the type of annotations.
    * 
    * @return an iterator of the given type of annotations. This method must <i>never</i> return <tt>null</tt>.
    */
   ListIterator<ResolvedAnnotation> iterator(String type);

   /**
    * Gets a set of all the annotationtypes in a field.
    *
    * @return a set of all the annotationtypes in a field.
    */
   Set<String> getAnnotationTypes();
}
