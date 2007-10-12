package no.trank.openpipe.api.document;

/**
 * @version $Revision: 874 $
 */
public class BaseResolvedAnnotation extends BaseAnnotation implements ResolvedAnnotation {
   private String fieldValue;
   private String value;

   public BaseResolvedAnnotation(Annotation annotation, String fieldValue) {
      super(annotation.getStartPos(), annotation.getEndPos());
      this.fieldValue = fieldValue;
   }

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
