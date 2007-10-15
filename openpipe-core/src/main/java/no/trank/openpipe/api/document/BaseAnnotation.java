package no.trank.openpipe.api.document;

/**
 * @version $Revision$
 */
public class BaseAnnotation implements Annotation {
   private int startPos;
   private int endPos;

   public BaseAnnotation() {
   }

   public BaseAnnotation(int startPos, int endPos) {
      this.startPos = startPos;
      this.endPos = endPos;
   }

   public int getStartPos() {
      return startPos;
   }

   public void setStartPos(int startPos) {
      this.startPos = startPos;
   }

   public int getEndPos() {
      return endPos;
   }

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
