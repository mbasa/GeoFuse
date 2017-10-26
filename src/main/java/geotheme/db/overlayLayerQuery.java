/**
 * パッケージ名：geotheme.db
 * ファイル名  ：overlayLayerQuery.java
 * 
 * @author mbasa
 * @since Dec 26, 2016
 */
package geotheme.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import geotheme.bean.*;

/**
 * 説明：
 *
 */
public class overlayLayerQuery {

    /**
     * コンストラクタ
     *
     */
    public overlayLayerQuery() {
    }

    public ArrayList<overlayLayerBean> getOverlayLayers() {
        ArrayList<overlayLayerBean> array = new ArrayList<overlayLayerBean>();
        
        Connection conn        = null;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;
        
        String sql = "select * from geofuse.overlaylayer where url is not null "
                + "and layers is not null and display = true order by rank";
        
        try {
            conn = connectionPoolHolder.getConnection();
            stmt = conn.prepareStatement(sql);
            rs   = stmt.executeQuery();
            
            if( rs != null ) {
                while( rs.next() ) {
                    overlayLayerBean blb = new overlayLayerBean();
                    
                    blb.setRank(rs.getInt("rank"));
                    blb.setUrl(rs.getString("url"));
                    blb.setLayers(rs.getString("layers"));
                    blb.setName(rs.getString("name"));
                    blb.setActive(rs.getBoolean("active"));
                    blb.setDisplay(rs.getBoolean("display"));
                    blb.setMinZoom(rs.getInt("minzoom"));

                    array.add(blb);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return array;
    }

}
