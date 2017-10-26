/**
 * パッケージ名：geotheme.geofuse_admin.db
 * ファイル名  ：DBTools.java
 * 
 * @author mbasa
 * @since May 31, 2017
 */
package geotheme.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 説明：
 *
 */
public class DBTools {

    /**
     * コンストラクタ
     *
     */
    public DBTools() {
    }

    public static Object getColumnValue(String sql,String columnName,Object...param) { 

        ScalarHandler<Object> kh = new ScalarHandler<Object>(columnName);

        Connection con = null;
        Object retVal  = null;

        try {
            con = connectionPoolHolder.getConnection();   
            QueryRunner runner = new QueryRunner();

            retVal = runner.query(con,sql,kh,param);
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        finally {
            if( con != null ) {
                connectionPoolHolder.returnConnection(con);
            }
        }

        return retVal;
    }

    public static Object[] getSingleRecord( String sql ) {
        return getSingleRecord(sql, (Object[])null);
    }

    public static Object[] getSingleRecord(String sql,Object...param) {
        Object retval[] = null;
        Logger LOGGER   = LogManager.getLogger();
        
        ResultSetHandler<Object[]> handler = new ResultSetHandler<Object[]>() {
            
            public Object[] handle(ResultSet rs) throws SQLException {
                
                if (!rs.next()) {
                    return null;
                }
            
                ResultSetMetaData meta = rs.getMetaData();
                int cols = meta.getColumnCount();
                Object[] result = new Object[cols];

                for (int i = 0; i < cols; i++) {
                    result[i] = rs.getObject(i + 1);
                }

                return result;
            }
        };
        
        Connection con = null;
        
        try {
            con = connectionPoolHolder.getConnection();
            
            QueryRunner runner = new QueryRunner();
            
            retval = runner.query(con, sql, handler,param);
        }
        catch (Exception e) {
            LOGGER.error( e );            
        }
        finally {
            if( con != null ) {
                connectionPoolHolder.returnConnection(con);
            }
        }
        return retval;
    }
    
    public static <T> List<T> getRecords(String sql,Class<T> obj) {
        return getRecords(sql, obj, (Object[])null);
    }
    
    public static <T> List<T> getRecords(String sql,Class<T> obj,Object... param) {
        Logger LOGGER = LogManager.getLogger();
        
        List<T> retval = null;
        
        Connection con = null;
        try {
            con = connectionPoolHolder.getConnection();
            
            ResultSetHandler<List<T>> rs = 
                    new BeanListHandler<T>(obj);
            
            QueryRunner runner = new QueryRunner();
            if( param == null ) {
                retval = runner.query(con,sql, rs);
            }
            else {
                retval = runner.query(con, sql, rs, param);
            }
        }
        catch( Exception e ) {
            LOGGER.error( e );
        }
        finally {
            if( con != null ) {
                connectionPoolHolder.returnConnection(con);
            }
        }
        return retval;
    }

    public static int update( String sql, Object... params ) {
        Logger LOGGER = LogManager.getLogger();
        LOGGER.debug("In update:{}",sql);
        
        Connection con = null;
        int retval     = -1;
        
        try {
            con = connectionPoolHolder.getConnection();
            con.setAutoCommit(true);
            
            QueryRunner runner = new QueryRunner();
            retval = runner.update(con,sql,params);
        }
        catch( Exception e ) {
            LOGGER.error( e );
        }
        finally {
            if( con != null ) {
                connectionPoolHolder.returnConnection(con);
            }
        }
        return retval;
    }


}
