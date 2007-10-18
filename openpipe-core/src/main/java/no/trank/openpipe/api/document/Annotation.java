package no.trank.openpipe.api.document;

/**
 * An interface that represents an annotation of a field-value.
 * <p/>
 * This <tt>Annotation</tt> annotates {@link AnnotatedField#getValue()}<tt>.substring(getStartPos(), getEndPos())</tt>.  
 * 
 * @version $Revision$
 */
public interface Annotation {

   /**
    * Gets the start index in the field-value.
    * 
    * @return the start index in the field-value.
    */
   int getStartPos();

   /**
    * Gets the end index (exclusive) in the field-value.
    * 
    * @return the end index (exclusive) in the field-value.
    */
   int getEndPos();
}
