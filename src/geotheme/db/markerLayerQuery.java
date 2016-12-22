/**
 * パッケージ名：geotheme.db
 * ファイル名  ：markerLayerQuery.java
 * 
 * @author mbasa
 * @since Dec 22, 2016
 */
package geotheme.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * 説明：
 *
 */
public class markerLayerQuery {

    /**
     * コンストラクタ
     *
     */
    public markerLayerQuery() {
    }

    public static ArrayList<Map<String,String>> getMarkerLayers() {
        
        ArrayList<Map<String,String>> retVal = 
                new ArrayList<Map<String,String>>();
        
        Connection conn        = null;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;
        
        String sql = "select layername,tablename from geofuse.markerlayer ";
        
        try {
            conn = connectionPoolHolder.getConnection();
            stmt = conn.prepareStatement(sql);
            rs   = stmt.executeQuery();
            
            if( rs != null ) {
                while( rs.next() ) {
                    Map<String,String> map = new HashMap<String,String>();
                    
                    map.put("layername", rs.getString("layername"));
                    map.put("tablename", rs.getString("tablename"));
                    
                    retVal.add(map);                    
                }
            }
        }
        catch( Exception ex ) {
            ex.printStackTrace();
        }
        finally {
            if( rs != null ) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if( stmt != null ) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if( conn != null ) {
                try {
                    //conn.close();
                    connectionPoolHolder.returnConnection(conn);
                } catch ( Exception e) {
                    e.printStackTrace();
                }
            }
        }        
        return retVal;
    }
    
    public static ArrayList<Map<String,String>> getMarkers(String tablename ) {
        ArrayList<Map<String,String>> retVal = 
                new ArrayList<Map<String,String>>();
        
        Connection conn        = null;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;
        
        String sql = "select * from  " + tablename ;
        
        try {
            conn = connectionPoolHolder.getConnection();
            stmt = conn.prepareStatement(sql);
            rs   = stmt.executeQuery();
            
            if( rs != null ) {
                
                ResultSetMetaData rsm  = rs.getMetaData();
                ArrayList<String> cols = new ArrayList<String>();
                
                for(int i=1;i<rsm.getColumnCount()+1;i++) {
                    cols.add(rsm.getColumnName(i));
                }
                
                while( rs.next() ) {
                    Map<String,String> map = new HashMap<String,String>();
                    
                    for( String col : cols ) {
                        map.put( col, rs.getString(col));
                    }
                    
                    retVal.add(map);                    
                }
            }
        }
        catch( Exception ex ) {
            ex.printStackTrace();
        }
        finally {
            if( rs != null ) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if( stmt != null ) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if( conn != null ) {
                try {
                    //conn.close();
                    connectionPoolHolder.returnConnection(conn);
                } catch ( Exception e) {
                    e.printStackTrace();
                }
            }
        }        
        return retVal;        
    }
}
