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

	private String hostname = null;
	private String username = null;
	private String password = null;
	private String dbname   = null;
	private String metaTab  = null;
	
	public metaDataCtl(String host,String user,
			String pass,String db,String tab) {
		this.hostname = host;
		this.username = user;
		this.password = pass;
		this.dbname   = db;
		this.metaTab  = tab;
	}
	
	public Connection getConnection() throws Exception{
		Class.forName("org.postgresql.Driver");

		String url = "jdbc:postgresql://"+this.hostname+"/"+this.dbname;

		Properties prop = new Properties();
		prop.setProperty("user", this.username);
		prop.setProperty("password", this.password);

		Connection con = DriverManager.getConnection(url,prop);
		
		return con;

	}
	
	public metaDataBean getMetaInfo(String layer)  {
		
		metaDataBean mdb = null;
		
		try {
			Connection con = this.getConnection();
			Statement stmt = con.createStatement();

			String sql = "select * from "+this.metaTab+ " where tabid = '"+
				layer+"'";

			ResultSet rs = stmt.executeQuery(sql);

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
			colns.append(colNames[i]).append(",");
		}
		
		colns.append(colNames[clength-1]);
		
		StringBuffer sb = new StringBuffer();
		sb.append("insert into ").append( metadata );
		sb.append(" values ( '").append(tabName).append("','");
		sb.append(linklayer).append("','");
		sb.append(maptable).append("','");
		sb.append(linkcolumn).append("','");
		sb.append(colns.toString()).append("',now(),");
		sb.append("'").append(mapType).append("'");
		sb.append(")");
		
		try {
			Connection con = this.getConnection();
			Statement  stmt= con.createStatement();
			
			stmt.executeUpdate(sb.toString());
			stmt.close();
			con.close();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public boolean processData(String[][] inData,String tabName) {
	
		StringBuffer createSt = new StringBuffer();
		createSt.append("create table ").append(tabName).append(" (");
		createSt.append("col0 varchar(100) Primary Key,");
		createSt.append("col1 float,");
		createSt.append("col2 float,");
		createSt.append("col3 float,");
		createSt.append("col4 float,");
		createSt.append("col5 float,");
		createSt.append("col6 float,");
		createSt.append("col7 float,");
		createSt.append("col8 float,");
		createSt.append("col9 float,");
		createSt.append("col10 float )");
		
		StringBuffer insertSt = new StringBuffer();
		
        insertSt.append("insert into ").append(tabName);
        insertSt.append(" values (?,?,?,?,?,?,?,?,?,?,?)");
        
		try {
			Connection con = this.getConnection();
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
								
				for( int j=1;j<clength;j++ ) {
					if( inData[i][j] != null && 
					        StringCheck.isNumeric(inData[i][j]) )
					    pstmt.setDouble(j+1,
					            Double.valueOf(inData[i][j]));
					else
						pstmt.setDouble(j+1, 0d);
				}

				for( int k=clength;k<11;k++) {
                    pstmt.setDouble(k+1,0d);
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
			exc.printStackTrace();
			return false;
		}
		
		return true;
	}

	public boolean processDataLatLon(String[][] inData,String tabName) {
		
		StringBuffer createSt = new StringBuffer();
		createSt.append("create table ").append(tabName).append(" (");
		createSt.append("col0 varchar(200),");
		createSt.append("col1 float, ");
		createSt.append("col2 float, ");
		createSt.append("col3 float, ");
		createSt.append("col4 float, ");
		createSt.append("col5 float, ");
		createSt.append("col6 float, ");
		createSt.append("col7 float, ");
		createSt.append("col8 float, ");
		createSt.append("col9 float, ");
		createSt.append("col10 float,");
		createSt.append("the_geom geometry )");
		
		StringBuffer insertSt;
		
		try {
			Connection con = this.getConnection();
			Statement stmt = con.createStatement();
			stmt.executeUpdate(createSt.toString());
			
			int clength;
			int rlength;
			String x,y;
			
			insertSt = new StringBuffer();
			insertSt.append("insert into ").append(tabName);
            insertSt.append(" values (?,?,?,?,?,?,?,?,?,?,?,");
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
				
				for( int j=1;j<clength;j++ ) {
					if( StringCheck.isNumeric(inData[i][j]) )
					    pstmt.setDouble(j+1,Double.valueOf(inData[i][j]));
					else
					    pstmt.setDouble(j+1,0d);
				}

				for( int k=clength;k<11;k++) {
				    pstmt.setDouble(k+1,0d);
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
								
				pstmt.setString(12, insertSt.toString());
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
			exc.printStackTrace();
			return false;
		}
		
		return true;
	}

	public String[] getLayerBnd(String mapTab,String linkTab,
			String linkCol ){
		
		String[] bnd = {"EPSG:4326","-180","-90","180","90"};

		try {
			Connection con = this.getConnection();
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
		
		return bnd;
	}
}
