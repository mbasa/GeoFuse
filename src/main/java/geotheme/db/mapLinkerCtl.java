/* 
 *   Copyright (C) May,2012  Mario Basa
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

public class mapLinkerCtl {
    
    private String dynTable      = null;
    
    public mapLinkerCtl( String dTable ) {
        this.dynTable = dTable;
    }
    
    public void setDynTable(String dynTable) {
        this.dynTable = dynTable;
    }

    public Map<String,String[]> getMapLinkData() {

        Map<String,String[]> dynamicMap = 
                new HashMap<String,String[]>();
        
        StringBuffer sql = new StringBuffer();
        sql.append("select * from ").append( this.dynTable );
        
        Connection conn        = null;
        PreparedStatement stmt = null;
        ResultSet rs           = null;
        		
        try {
            conn = connectionPoolHolder.getConnection();            
            stmt = conn.prepareStatement( sql.toString() );            
            rs   = stmt.executeQuery();
            
            if( rs != null ) {
                while( rs.next() ) {
                    String arr[] = new String[3];
                    arr[0] = rs.getString(2);
                    arr[1] = rs.getString(3);
                    arr[2] = rs.getString(4);
                    
                    dynamicMap.put(rs.getString(1), arr);
                }
            }            
        }
        catch(Exception ex) {
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
        return dynamicMap;
    }

}
