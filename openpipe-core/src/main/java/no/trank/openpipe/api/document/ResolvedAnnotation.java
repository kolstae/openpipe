package no.trank.openpipe.api.document;

/**
 * An interface describing a resolved annotation, an annotation with a value.
 * 
 * @version $Revision$
 */
public interface ResolvedAnnotation extends Annotation {

   /**
    * Gets the value of this annotation.
    * 
    * @return the value of this annotation.
    */
   String getValue();
}
