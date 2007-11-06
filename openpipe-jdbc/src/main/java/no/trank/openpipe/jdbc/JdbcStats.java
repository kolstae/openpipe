package no.trank.openpipe.jdbc;

/**
 * @version $Revision$
 */
public interface JdbcStats {

   void startPreSql();

   void startIt();

   void startPostSql();

   void stop();

   void incr(String operation);

   void startFailSql();
}
