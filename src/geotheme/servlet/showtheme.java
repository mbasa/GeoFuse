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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import geotheme.bean.*;
import geotheme.db.*;
import geotheme.util.*;
/**
 * Servlet implementation class showtheme
 */
public class showtheme extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
//	private String geoserverURL = new String();
	private String googleKey    = new String();
	private String colorNames   = new String();
	private String labelScale   = new String();
	private String themeRanges  = new String();
	
	private String db_name      = new String();
	private String db_host      = new String();
	private String db_user      = new String();
	private String db_password  = new String();
	private String db_metadata  = new String();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public showtheme() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, 
	 * HttpServletResponse response)
	 */
	protected void service(HttpServletRequest req, 
	        HttpServletResponse res) throws ServletException, IOException {

	    String thisUrl   = UrlUtil.getUrl(req);
	    String layerName = new String();
	    
	    if(req.getParameter("layer") != null &&
	       req.getParameter("layer").length() > 1 ) {
	        layerName = req.getParameter("layer");
	    }	    	    
	    
	    metaDataCtl mdc = new metaDataCtl(this.db_host,
	    		this.db_user,this.db_password,
	    		this.db_name,this.db_metadata);
	    
	    metaDataBean mdb = mdc.getMetaInfo(layerName);
	    
	    if( mdb == null ) {
	    	PrintWriter out = res.getWriter();
	    	out.write("Layer "+layerName+" not found in Catalog.");
	    	out.close();
	    	return;
	    }
	    
	    String bnd[] = mdc.getLayerBnd(mdb.getMapTable(), 
	    		mdb.getTabid(), mdb.getLinkColumn());
	    
	    StringBuffer viewParams = new StringBuffer();
	    
	    viewParams.append("linktab:").append(mdb.getTabid()).append(";");
	    viewParams.append("maptab:").append(mdb.getMapTable()).append(";");
	    viewParams.append("mapcol:").append(mdb.getLinkColumn());
	    
	    themeBean tb = new themeBean();
	    
	    tb.setLayerName(mdb.getLinkLayer());
	    tb.setViewParams(viewParams.toString() );
	    tb.setGoogleKey(this.googleKey);
	    tb.setColorNames(this.colorNames);
	    tb.setGsldUrl(thisUrl + "/gsld");
	    tb.setWmsUrl(thisUrl  + "/wms");
	    
	    tb.setPropList( new ArrayList<String>( 
	    		Arrays.asList(mdb.getColNames()) ) );
	    
	    tb.setBounds(bnd);
	    tb.setLayerType(mdb.getLayerType());
	    
	    tb.setLabelScale(this.labelScale);
	    tb.setThemeRanges(this.themeRanges);
	    
	    req.setAttribute("themeBean", tb);
	    
	    RequestDispatcher dispatcher = 
	        getServletContext().getRequestDispatcher("/jsp/showtheme.jsp");
	    
	    dispatcher.forward(req,res);
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		
		ResourceBundle rb = 
			ResourceBundle.getBundle("properties.thematic");
		
		ResourceBundle rdb = 
				ResourceBundle.getBundle("properties.database");
		
//		this.geoserverURL = rb.getString("GEOSERVER.BASE.URL");

		this.colorNames   = rb.getString("THEMATIC.COLOR.NAMES");
		this.labelScale   = rb.getString("THEMATIC.LABEL.MAXSCALE");
		this.themeRanges  = rb.getString("THEMATIC.RANGES");
		
		this.db_name      = rdb.getString("DB.NAME");
		this.db_host      = rdb.getString("DB.HOST");
		this.db_user      = rdb.getString("DB.USER");
		this.db_password  = rdb.getString("DB.PASSWORD");
		this.db_metadata  = rdb.getString("DB.METADATA.TABLE");
		
    	try {
    		byte[] byteData = this.colorNames.getBytes("ISO_8859_1");
    		this.colorNames = new String(byteData, "UTF-8");
    	}
    	catch(Exception e){
    		System.out.println(e);
    	}

	}
}
