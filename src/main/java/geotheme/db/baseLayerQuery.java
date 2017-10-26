/**
 * 
 */
package geotheme.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import geotheme.bean.baseLayerBean;

/**
 * @author mbasa
 *
 */
public class baseLayerQuery {
	
	/**
	 * 
	 */
	public baseLayerQuery() {
		
	}
	
	public ArrayList<baseLayerBean> getBaseLayers() {
		ArrayList<baseLayerBean> array = new ArrayList<baseLayerBean>();
		
		Connection conn        = null;
		PreparedStatement stmt = null;
		ResultSet         rs   = null;
		
		String sql = "select * from geofuse.baselayer "
				+ "where url is not null and display = true order by rank";
		
		try {
			conn = connectionPoolHolder.getConnection();
			stmt = conn.prepareStatement(sql);
			rs   = stmt.executeQuery();
			
			if( rs != null ) {
				while( rs.next() ) {
					baseLayerBean blb = new baseLayerBean();
					
					blb.setRank(rs.getInt("rank"));
					blb.setUrl(rs.getString("url"));
					blb.setAttribution(rs.getString("attribution"));
					blb.setSubDomain(rs.getString("subdomain"));
					blb.setName(rs.getString("name"));
					blb.setDisplay(rs.getBoolean("display"));

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
				} catch ( Exception e) {
					e.printStackTrace();
				}
			}
		}
		return array;
	}

}
