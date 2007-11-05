package no.trank.openpipe.api.document;

/**
 * A basic implementation of {@link ResolvedAnnotation} with a pre-resolved value.
 * 
 * @version $Revision$
 */
public class PreResolvedAnnotation extends BaseAnnotation implements ResolvedAnnotation {
   private final String value;

   /**
    * Constructs a <tt>PreResolvedAnnotation</tt> with a given pre-resolved value and <tt>startPos = endPos = 0</tt>.
    * 
    * @param value the pre-resolved value.
    */
   public PreResolvedAnnotation(String value) {
      this(0, 0, value);
   }

   /**
    * Constructs a <tt>PreResolvedAnnotation</tt> with a given pre-resolved value.
    * 
    * @param value the pre-resolved value.
    */
   public PreResolvedAnnotation(int startPos, int endPos, String value) {
      super(startPos, endPos);
      this.value = value;
   }

   @Override
   public String getValue() {
      return value;
   }
}
