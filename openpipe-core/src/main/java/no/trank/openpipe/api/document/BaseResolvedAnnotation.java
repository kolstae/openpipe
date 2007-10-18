package no.trank.openpipe.api.document;

/**
 * A basic implementation of {@link ResolvedAnnotation}.
 * 
 * @version $Revision$
 */
public class BaseResolvedAnnotation extends BaseAnnotation implements ResolvedAnnotation {
   private String fieldValue;
   private String value;

   /**
    * Constructs a <tt>BaseResolvedAnnotation</tt> from the given annotation and field-value.
    * <p/>
    * Uses <tt>fieldValue.substring(getStartPos(), getEndPos())</tt> to find its value
    * 
    * @param annotation the annotation to resove.
    * @param fieldValue the value to resolve from.
    */
   public BaseResolvedAnnotation(Annotation annotation, String fieldValue) {
      super(annotation.getStartPos(), annotation.getEndPos());
      this.fieldValue = fieldValue;
   }

   /**
    * {@inheritDoc}
    * <p/>
    * On first access the value is resolved as described {@link #BaseResolvedAnnotation(Annotation, String) here}. 
    * 
    * @see #BaseResolvedAnnotation(Annotation, String) 
    */
   public String getValue() {
      if (value == null) {
         value = fieldValue.substring(getStartPos(), getEndPos());
         fieldValue = null;
      }
      return value;
   }

   @Override
   public String toString() {
      return "BaseResolvedAnnotation{" +
            "value='" + getValue() + '\'' +
            '}';
   }
}
