package no.trank.openpipe.jdbc;

/**
 * @version $Revision: 874 $
 */
public class NoopJdbcStats implements JdbcStats {

   public void startPreSql() {
   }

   public void startIt() {
   }

   public void startPostSql() {
   }

   public void stop() {
   }

   public void incr(String operation) {
   }
}
