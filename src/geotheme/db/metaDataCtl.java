/* 
 *	 Copyright (C) May,2012  Mario Basa
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package geotheme.db;

import java.sql.*;
import java.util.*;
import geotheme.bean.*;
import geotheme.util.*;

public class metaDataCtl {


	private String metaTab       = null;
	private connectionCtl conCtl = null;
	
	public metaDataCtl(connectionCtl conCtl,String tab) {
		this.metaTab  = tab;
		this.conCtl   = conCtl;
	}
	
	public metaDataBean getMetaInfo(String layer)  {
		
		metaDataBean mdb = null;
		
        String sql = "select * from " + this.metaTab + " where tabid = ?";

		try {
			Connection con         = this.conCtl.getConnection();
			PreparedStatement stmt = con.prepareStatement( sql );
			
			stmt.setString(1, layer);
			
			ResultSet rs = stmt.executeQuery();

			if( rs != null ) {
				while( rs.next() ) {
					mdb = new metaDataBean();
					mdb.setTabid( rs.getString("tabid") );
					mdb.setLinkLayer(rs.getString("linklayer"));
					mdb.setMapTable(rs.getString("maptable"));
					mdb.setLinkColumn(rs.getString("linkcolumn"));
					mdb.setColNames( rs.getString("colnames").split(",") );
					mdb.setLayerType(rs.getString("layertype"));
				}
			}
			
			rs.close();
			stmt.close();
			con.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return mdb;
	}
	
	public boolean setMetaInfo(String colNames[],Map<String,String[]> dMap,
			String metadata,String tabName,boolean isLatLon ) {
		
		String linkcolumn = colNames[0];
		String arr[]      = null;
		
		if(!isLatLon)
			arr = dMap.get(linkcolumn);
		else
			arr = dMap.get("latlon");
		
		String maptable   = arr[0];
		String linklayer  = arr[1];
		String mapType    = arr[2];
		
		StringBuffer colns = new StringBuffer();
		
		int clength = colNames.length;
		
		if( colNames[clength-1].isEmpty() )
			clength--;
		
		if( isLatLon ) 
			clength = clength-2;
		
		if( clength > 11 )
			clength = 11;
		
		for(int i=1;i<clength-1;i++){
		    if(colNames[i].compareToIgnoreCase("timestamp") != 0) {
		        colns.append(colNames[i]).append(",");
		    }
		}
		
		if(colNames[clength-1].compareToIgnoreCase("timestamp") != 0) {
		    colns.append(colNames[clength-1]);
		}
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("insert into ").append(metadata);
        sb.append(" values (?,?,?,?,?,now(),?);");

        try {
			Connection con         = this.conCtl.getConnection();
			PreparedStatement stmt = con.prepareStatement(sb.toString());
			
			stmt.setString(1, tabName);
			stmt.setString(2, linklayer);
			stmt.setString(3, maptable);
			stmt.setString(4, linkcolumn);
			stmt.setString(5, colns.toString());
			stmt.setString(6, mapType);
			
			stmt.executeUpdate();
			stmt.close();
			con.close();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public String processData(String[][] inData,String tabName) {
	
		StringBuffer createSt = new StringBuffer();
		createSt.append("create table ").append(tabName).append(" (");
		createSt.append("col0  text Primary Key,");
		createSt.append("col1  float,");
		createSt.append("col2  float,");
		createSt.append("col3  float,");
		createSt.append("col4  float,");
		createSt.append("col5  float,");
		createSt.append("col6  float,");
		createSt.append("col7  float,");
		createSt.append("col8  float,");
		createSt.append("col9  float,");
		createSt.append("col10 float,");
		createSt.append("intime timestamp)");
		
		StringBuffer insertSt = new StringBuffer();
		
        insertSt.append("insert into ").append(tabName);
        insertSt.append(" values (?,?,?,?,?,?,?,?,?,?,?,?)");
        
        String  retVal = null;
        Connection con = null;
        
		try {
			 con = this.conCtl.getConnection();
			
			Statement stmt = con.createStatement();
			stmt.executeUpdate(createSt.toString());
			
			PreparedStatement pstmt = 
			        con.prepareStatement(insertSt.toString());
			
			int clength;
			
			for(int i=1;i<inData.length;i++) {
				
				clength = inData[i].length;
				
				if( clength > 11 )
					clength = 11;
			
				pstmt.setString(1, inData[i][0]);
								
				int colCount     = clength;
				String timestamp = null;
				
				for( int j=1,k=1;j<clength;j++,k++ ) {
				    
				    if( inData[0][j].compareToIgnoreCase("timestamp") == 0)  {
				        timestamp = inData[i][j];
				        colCount--;
				        k--;
				        continue;
				    }
				    
					if( inData[i][j] != null && 
					        StringCheck.isNumeric(inData[i][j]) )
					    pstmt.setDouble(k+1,
					            Double.valueOf(inData[i][j]));
					else
						pstmt.setDouble(k+1, 0d);
				}

				for( int l=colCount;l<11;l++) {
                    pstmt.setDouble(l+1,0d);
                }

				Timestamp ts = this.validateTimestamp(timestamp);
				
				if( ts != null ) {
				    pstmt.setTimestamp(12, ts);
				}
				else {
				    pstmt.setNull(12,Types.TIMESTAMP);
				}
				
				pstmt.addBatch();
				
				if( i % 200 == 0 )
				    pstmt.executeBatch();
			}
			pstmt.executeBatch();
			
			stmt.close();
			pstmt.close();
			con.close();
		}
		catch( Exception exc ) {
			retVal = exc.getLocalizedMessage();
		}
		finally {
		    try{
		        if( con != null )
		            con.close();
		    }
		    catch( Exception ee ) {}
		}
		
		return retVal;
	}

	public Timestamp validateTimestamp(String timestamp) {
	    
	    Timestamp retval = null;
	    
	    if( timestamp == null || timestamp.length() < 10 )
	        return null;
	    
	    if(timestamp.length() == 10)
            timestamp = timestamp.concat(" 00:00:00");
	    
	    if(timestamp.length() < 19 )
	        return null;
	    
	    if( timestamp.length() >= 20  )
	        timestamp = timestamp.substring(0,19);
	       // timestamp = timestamp.substring(0, 19)+
	       //     "+"+timestamp.substring(20,timestamp.length());
	    
	    timestamp = timestamp.replace('/', '-');
	    
	    try {
	        retval = Timestamp.valueOf( timestamp );
	    }
	    catch( Exception e) {}
	    
	    return retval;
	}
	
	public String processDataLatLon(String[][] inData,String tabName) {
		
		StringBuffer createSt = new StringBuffer();
		createSt.append("create table ").append(tabName).append(" (");
		createSt.append("col0     text,");
		createSt.append("col1     float, ");
		createSt.append("col2     float, ");
		createSt.append("col3     float, ");
		createSt.append("col4     float, ");
		createSt.append("col5     float, ");
		createSt.append("col6     float, ");
		createSt.append("col7     float, ");
		createSt.append("col8     float, ");
		createSt.append("col9     float, ");
		createSt.append("col10    float,");
		createSt.append("intime   timestamp,");
		createSt.append("the_geom geometry )");
		
		StringBuffer insertSt;
		
		Connection con = null;
		String retVal  = null;
		
		try {
			con = this.conCtl.getConnection();
			Statement stmt = con.createStatement();
			stmt.executeUpdate(createSt.toString());
			
			int clength;
			int rlength;
			String x,y;
			
			insertSt = new StringBuffer();
			insertSt.append("insert into ").append(tabName);
            insertSt.append(" values (?,?,?,?,?,?,?,?,?,?,?,?,");
            insertSt.append(" ST_GeomFromText(?,4326))");
            
			PreparedStatement pstmt = 
			        con.prepareStatement(insertSt.toString());
			
			for(int i=1;i<inData.length;i++) {
				
				clength = inData[i].length;
				
				if( inData[i][clength-1].isEmpty() )
					clength--;
				
				rlength = clength;
				clength = clength-2;
				
				if( clength > 11 )
					clength = 11;
								
				pstmt.setString(1, inData[i][0]);
				
				int colCount     = clength;
                String timestamp = null;
                
				for( int j=1,k=1;j<clength;j++,k++ ) {
				    
				    if( inData[0][j].compareToIgnoreCase("timestamp") == 0)  {
                        timestamp = inData[i][j];
                        colCount--;
                        k--;
                        continue;
                    }
				    
					if( StringCheck.isNumeric(inData[i][j]) )
					    pstmt.setDouble(k+1,Double.valueOf(inData[i][j]));
					else
					    pstmt.setDouble(k+1,0d);
				}

				for( int l=colCount;l<11;l++) {
				    pstmt.setDouble(l+1,0d);
				}
				
				if( StringCheck.isNumeric(inData[i][rlength-2]) )
					x = inData[i][rlength-2];
				else
					x = "0";
				
				if( StringCheck.isNumeric(inData[i][rlength-1]) )
					y = inData[i][rlength-1];
				else
					y = "0";

				insertSt = new StringBuffer();
				insertSt.append("POINT("); 
				insertSt.append(x).append(" ");
				insertSt.append(y).append(")");
								
				pstmt.setString(13, insertSt.toString());
				
				Timestamp ts = this.validateTimestamp(timestamp);
				
				if( ts != null ) {
                    pstmt.setTimestamp(12, ts);
                }
                else {
                    pstmt.setNull(12,Types.TIMESTAMP);
                }
				
				pstmt.addBatch();
				
				if( i % 200 == 0)
				    pstmt.executeBatch();				
			}
			
			pstmt.executeBatch();
			
			stmt.close();
			pstmt.close();
			con.close();
		}
		catch( Exception exc ) {
			retVal = exc.getLocalizedMessage();
		}
		finally {
		    try{
		        if( con != null )
		            con.close();
		    }
		    catch( Exception ee ) {}
		}
		
		return retVal;
	}

	public String[] getTimestampStat(String table) {
	    String retVal[] = new String[2];
	    StringBuffer sb = new StringBuffer();
	    
	    sb.append("select min(intime) min,max(intime) max from ");
	    sb.append(table);
	    
	    retVal[0]      = null;
	    retVal[1]      = null;        
	    Connection con = null;
	    
	    try {
	        con = this.conCtl.getConnection();
	        Statement stmt = con.createStatement();
	        ResultSet rs   = stmt.executeQuery(sb.toString());

	        if( rs != null ) {
	            while( rs.next() ) {
	                retVal[0] = rs.getString("min");
	                retVal[1] = rs.getString("max");
	            }
	        }
	    }
	    catch(Exception e) {
	        e.printStackTrace();
	    }
	    finally {
	        try{
	            if(con!=null)
	                con.close();
	        }
	        catch(Exception ee) {}
	    }
	    return retVal;
	}
	
	public String[] getLayerBnd(String mapTab,String linkTab,
			String linkCol ){
		
		String[] bnd = {"EPSG:4326","-180","-90","180","90"};

		Connection con = null;
		
		try {
			con = this.conCtl.getConnection();
			Statement stmt = con.createStatement();

			StringBuffer sql = new StringBuffer();
			
			if(mapTab.compareToIgnoreCase("latlon")!=0) {
				sql.append("select st_extent(b.the_geom) as ext from ");
				sql.append( linkTab ).append(" a, ");
				sql.append( mapTab ).append(" b where ");
				sql.append("a.col0");
				sql.append("=");
				sql.append("b.").append( linkCol );
			}
			else {
				sql.append("select st_extent(the_geom) as ext from ");
				sql.append(linkTab);
			}
			
			ResultSet rs = stmt.executeQuery(sql.toString());

			if( rs != null ) {
				while( rs.next() ) {
					String st = rs.getString("ext");
					st = st.replace("BOX(", "");
					st = st.replace(")", "");
					
					String pts[] = st.split(",");
					String pt1[] = pts[0].split(" ");
					String pt2[] = pts[1].split(" ");
					
					bnd[0] = "EPSG:4326";
					bnd[1] = pt1[0];
					bnd[2] = pt1[1];
					bnd[3] = pt2[0];
					bnd[4] = pt2[1];
				}
			}
			
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		finally {
		    try{
		        if( con != null )
		            con.close();
		    }
		    catch( Exception ee) {}
		}
		
		return bnd;
	}
}
