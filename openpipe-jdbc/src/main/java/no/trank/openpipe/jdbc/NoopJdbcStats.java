package no.trank.openpipe.jdbc;

/**
 * @version $Revision$
 */
public class NoopJdbcStats implements JdbcStats {

   @Override
   public void startPreSql() {
   }

   @Override
   public void startIt() {
   }

   @Override
   public void startPostSql() {
   }

   @Override
   public void stop() {
   }

   @Override
   public void incr(String operation) {
   }

   @Override
   public void startFailSql() {
   }
}
