package no.trank.openpipe.api.document;

/**
 * A basic implementation of {@link Annotation}.
 * 
 * @version $Revision$
 */
public class BaseAnnotation implements Annotation {
   private int startPos;
   private int endPos;

   /**
    * Constructs a <tt>BaseAnnotation</tt>.
    */
   public BaseAnnotation() {
   }

   /**
    * Constructs a <tt>BaseAnnotation</tt> with the given start and end positions.
    */
   public BaseAnnotation(int startPos, int endPos) {
      this.startPos = startPos;
      this.endPos = endPos;
   }

   public int getStartPos() {
      return startPos;
   }

   /**
    * Sets the start position of this annotation.
    * 
    * @param startPos the start position of this annotation.
    * 
    * @see Annotation#getStartPos()
    */
   public void setStartPos(int startPos) {
      this.startPos = startPos;
   }

   public int getEndPos() {
      return endPos;
   }

   /**
    * Sets the end position (exclusive) of this annotation.
    * 
    * @param endPos the end position (exclusive) of this annotation.
    * 
    * @see Annotation#getEndPos()
    */
   public void setEndPos(int endPos) {
      this.endPos = endPos;
   }

   @Override
   public String toString() {
      return "BaseAnnotation{" +
            "startPos=" + startPos +
            ", endPos=" + endPos +
            '}';
   }
}
