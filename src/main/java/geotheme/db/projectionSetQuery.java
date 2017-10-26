/**
 * パッケージ名：geotheme.db
 * ファイル名  ：projectionSetQuery.java
 * 
 * @author mbasa
 * @since Aug 17, 2015
 */
package geotheme.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 説明：
 *
 */
public class projectionSetQuery {

	/**
	 * コンストラクタ
	 *
	 */
	public projectionSetQuery() {
	}

	public static double[] changeProjection(double x,double y,
			int fromEpsg, int toEpsg) {
		
		double retval[] = {-9999.9999f,-9999.99999f};
		
		Connection conn        = null;
		PreparedStatement stmt = null;
		ResultSet rs           = null;

		StringBuffer sb = new StringBuffer();
		
		sb.append("select st_x(geom) as x,st_y(geom) as y from ");
		sb.append("(select st_transform(st_setsrid(st_point(?,?),?),?) ");
		sb.append("as geom) as aa");
		
		try {
			
			conn = connectionPoolHolder.getConnectionPool().reserveConnection();
			stmt = conn.prepareStatement( sb.toString() );
			
			stmt.setDouble(1, x);
			stmt.setDouble(2, y);
			stmt.setInt(3, fromEpsg);
			stmt.setInt(4, toEpsg);
			
			rs = stmt.executeQuery();
			
			if( rs != null) {
				while( rs.next() ) {
					retval[0] = rs.getDouble(1);
					retval[1] = rs.getDouble(2);
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
					connectionPoolHolder.getConnectionPool().releaseConnection(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return retval;
	}
}
