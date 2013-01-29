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

package geotheme.servlet;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import geotheme.util.UrlUtil;
import geotheme.csv.parseCSV;
import geotheme.db.*;

/**
 * Servlet implementation class inputData
 */
public class inputData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	//private Map<String,String[]> dynamicMap = new HashMap<String,String[]>();
	
	private String db_name      = new String();
	private String db_host      = new String();
	private String db_user      = new String();
	private String db_password  = new String();
	private String db_metadata  = new String();
	private String db_dynlinker = new String();
	private String db_csvdata_schema = new String();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public inputData() {
        super();
        
        ResourceBundle rdb = ResourceBundle.getBundle(
        		"properties.database");
                
        this.db_name           = rdb.getString("DB.NAME");
		this.db_host           = rdb.getString("DB.HOST");
		this.db_user           = rdb.getString("DB.USER");
		this.db_password       = rdb.getString("DB.PASSWORD");
		this.db_metadata       = rdb.getString("DB.METADATA.TABLE");
		this.db_dynlinker      = rdb.getString("DB.DYNAMIC_LINKER.TABLE");
		this.db_csvdata_schema = rdb.getString("DB.CSVDATA_SCHEMA");
		
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, 
	 * HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, 
			HttpServletResponse response) 
	throws ServletException, IOException {
		
		String inputString = request.getParameter("data");
		String mapType     = request.getParameter("type");
		
		response.setContentType("text/text");
		PrintWriter out  = response.getWriter();

		boolean isLatLon = false;
	      
		connectionCtl conCtl = new connectionCtl(this.db_host,this.db_user,
	                this.db_password,this.db_name);

		if( inputString == null || inputString.length() < 10  ) {
			out.write("Error: not enough input data");
			out.close();
			return;
		}
		
		inputString = (URLDecoder.decode(inputString,"UTF-8"));

		String pStr[][] = parseCSV.parse(inputString);
		
		if(pStr == null ) {
			out.write("Error: problem with input data");
			out.close();
			return;
		}
		
		mapLinkerCtl dlc = new mapLinkerCtl(conCtl,this.db_dynlinker);
		Map<String,String[]> dynamicMap = dlc.getMapLinkData();
		
		if( !dynamicMap.containsKey(pStr[0][0]) ) {
			int pStrLen = pStr[0].length;
			
			if( pStr[0][pStrLen-1].isEmpty() )
				pStrLen--;
			
			String cLon = pStr[0][pStrLen-2];
			String cLat = pStr[0][pStrLen-1];
			
			if(cLon.equalsIgnoreCase("lon") && 
               cLat.equalsIgnoreCase("lat") ) {
				
				isLatLon = true;
				
			}
			else {
				out.write("Error: 1st column does not match any Mapped column");
				out.close();
				return;			
			}
		}
				
		metaDataCtl mdc = new metaDataCtl(conCtl,this.db_metadata);
		
		Random rand = new Random();
		
		String sessionID = request.getSession(true).getId();
		String tableName = this.db_csvdata_schema + 
		        ".mb_"+sessionID+"_"+rand.nextInt(10000);
		
		if( !mdc.setMetaInfo(pStr[0], dynamicMap, this.db_metadata, 
		        tableName,isLatLon) ) {
			out.write("Error: Catalog entry");
			out.close();
			return;
		}
		
		if( isLatLon ) {
		    String rs = mdc.processDataLatLon(pStr, tableName); 
			if( rs != null ) {
				out.write("Error Data entry: "+rs);
				out.close();
				return;
			}
		}
		else {
		    String rs = mdc.processData(pStr, tableName);
			if( rs != null ) {
				out.write("Error Data entry: "+rs);
				out.close();
				return;
			}
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(UrlUtil.getUrl(request));
		
		if( mapType != null && mapType.compareToIgnoreCase("zd")==0 )
			sb.append("/zshowtheme?layer=");
		else
			sb.append("/showtheme?layer=");
		sb.append(tableName);
		
		out.write(sb.toString());
		out.close();
	}

}
