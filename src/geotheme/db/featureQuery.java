/**
 * 
 */
package geotheme.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import geotheme.bean.themeBean;

/**
 * @author mbasa
 *
 */
public class featureQuery {

	private themeBean tb = null;
	private double tolerance;
	private String propName;
	private double minVal;
	private double maxVal;
	
	/**
	 * 
	 */
	public featureQuery(themeBean tb,double tolerance,
			String propName, double minVal, double maxVal ) {

		this.tb        = tb;
		this.tolerance = tolerance;
		this.propName  = propName;
		this.minVal    = minVal;
		this.maxVal    = maxVal;
	}
	
	public LinkedHashMap<String,String> getFeature(double x,double y) {
		
		StringBuffer sb = new StringBuffer();
		LinkedHashMap<String,String> retVal = null;
		
		Connection conn        = null;
		PreparedStatement stmt = null;
		ResultSet rs           = null;
		
		if( this.tb.getLayerType().equalsIgnoreCase("polygon") ) {
			sb.append("select a.* from ").append(this.tb.getLinkTab()).append(" a,");
			sb.append(this.tb.getMapTab()).append(" b ");
			sb.append("where a.col0 = ").append(this.tb.getMapCol()).append(" and ");
			sb.append("ST_CONTAINS(b.the_geom,ST_SETSRID(ST_MAKEPOINT(?,?),4326)) ");
			//sb.append(this.tb.getSrs()).append("))");
			
			if( tb.getCql().length() >= 10 ) {
				sb.append("and ").append( tb.getCql() ).append(" ");
			}
			
			sb.append("and ").append(this.propName).
				append(" >= ").append(this.minVal).append(" ");
			sb.append("and ").append(this.propName).
				append(" <= ").append(this.maxVal).append(" ");
		}
		else if( this.tb.getLayerType().equalsIgnoreCase("point") ) {
			sb.append("select a.*,st_x(the_geom) as lon,st_y(the_geom) as lat from ");
			sb.append(this.tb.getLinkTab()).append(" a ");
			sb.append("where ST_DWITHIN(the_geom,ST_SETSRID(ST_MAKEPOINT(?,?),4326),");
			sb.append(this.tolerance).append(",true) ");
			
			if( tb.getCql().length() >= 10 ) {
				sb.append("and ").append( tb.getCql() ).append(" ");
			}

			sb.append("and ").append(this.propName).
				append(" >= ").append(this.minVal).append(" ");
			sb.append("and ").append(this.propName).
				append(" <= ").append(this.maxVal).append(" ");

			sb.append("limit 1 ");
		}
		else if( this.tb.getLayerType().equalsIgnoreCase("line") ) {
			sb.append("select *,st_x(p_geom) as lon,st_y(p_geom) as lat from ");
			sb.append("(select a.*,st_closestpoint(b.the_geom,c.pt) as p_geom  ");
			sb.append("from ").append(this.tb.getLinkTab()).append(" a,");
			sb.append(this.tb.getMapTab()).append(" b, ");
			sb.append("(select ST_SETSRID(ST_MAKEPOINT(?,?),4326) as pt) as c ");
			sb.append("where col0 = ").append(this.tb.getMapCol()).append(" ");
			sb.append("and ST_DWITHIN(b.the_geom,c.pt,");
			sb.append(this.tolerance).append(",true) ");
			
			if( tb.getCql().length() >= 10 ) {
				sb.append("and ").append( tb.getCql() ).append(" ");
			}
			
			sb.append("and ").append(this.propName).
				append(" >= ").append(this.minVal).append(" ");
			sb.append("and ").append(this.propName).
				append(" <= ").append(this.maxVal).append(" ");

			sb.append("limit 1) as q");
		}
		
		if( sb.length() > 0 ) {			
			
			try {
				conn = connectionPoolHolder.getConnection();	            
	            stmt = conn.prepareStatement( sb.toString() );
	            stmt.setDouble(1, x);
	            stmt.setDouble(2, y);

	            rs = stmt.executeQuery();

	            ResultSetMetaData rsmd = rs.getMetaData();
	            
	            if( rs != null && rsmd.getColumnCount() > 0 ) {
	            	
	            	retVal = new LinkedHashMap<String,String>();
	            	
	            	ArrayList<String> colList = new ArrayList<String>();
	            	
	            	for(int i=0;i<rsmd.getColumnCount();i++) {
	            		colList.add(rsmd.getColumnName(i+1));
	            	}
	            	
	            	while(rs.next()){
	            		
	            		retVal.put(this.tb.getMapCol(), rs.getString("col0"));
	            		
	            		for(String s : this.tb.getPropMap().keySet() ) {
	            		    double d = rs.getDouble(s);
                            retVal.put(this.tb.getPropMap().get(s), Double.toString(d));
	            		}
	            		
	            		if( colList.contains("intime") && 
	            				rs.getString("intime") != null ) { 
	            			retVal.put("timestamp", rs.getString("intime"));
	            		}
	            		if( colList.contains("lon") ) { 
	            		    double d = rs.getDouble("lon");
	            		    retVal.put("lon", Double.toString(d));
	            		}
	            		if( colList.contains("lat") ) { 
	            		    double d = rs.getDouble("lat");
	            		    retVal.put("lat", Double.toString(d));
	            		}
	            		break;
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
		}
		return retVal;
	}

}
