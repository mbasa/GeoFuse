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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.awt.Color;
import java.util.*;

import geotheme.wfsUtil.wfsRangeList;
import geotheme.color.rangeColor;
import geotheme.sld.thematicSld;

/**
 * Servlet implementation class gsld
 */
public class generateSLD extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private String geoserverURL = new String();
	private Map<String,String[]> colorMap = new HashMap<String,String[]>();
	
	
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		
		ResourceBundle rb = 
			ResourceBundle.getBundle("properties.thematic");
		
		this.geoserverURL = rb.getString("GEOSERVER.BASE.URL");
		
		String[] colorNames= rb.getString("THEMATIC.COLOR.NAMES").split(",");
		String[] colorVals = rb.getString("THEMATIC.COLORS").split(",");

		if( colorNames.length == colorVals.length ) {
			for(int i=0;i<colorNames.length;i++) {
				try {
		    		byte[] byteData = colorNames[i].getBytes("ISO_8859_1");
		    		colorNames[i] = new String(byteData, "UTF-8");
		    	}
		    	catch(Exception e){
		    		System.out.println(e);
		    	}
				this.colorMap.put(colorNames[i], colorVals[i].split("-"));
			}
		}		
	}

    /**
     * @see HttpServlet#HttpServlet()
     */
    public generateSLD() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        Color colorFrom = Color.YELLOW;
        Color colorTo = Color.RED;
        String typeName = new String();
        String geoType  = new String();
        String propName = new String();
        String typeRange = "EQRange";
        String cqlString = "";
        String viewParams= "";
        String labScale  = "";
        int numRange = 6;

        // ************************************
        // * Getting Request Parameters
        // ************************************
        if (request.getParameter("typename") != null) {
            typeName = request.getParameter("typename");
        }
        if (request.getParameter("geotype") != null) {
            geoType = request.getParameter("geotype");
        }
        if (request.getParameter("propname") != null) {
        	propName = request.getParameter("propname");
        	try {
        		byte[] byteData = propName.getBytes("ISO_8859_1");
        		propName = new String(byteData, "UTF-8");
        	}
        	catch(Exception e){
        		System.out.println(e);
        	}
        }
        if (request.getParameter("typerange") != null) {
            typeRange = request.getParameter("typerange");
        }
        if(request.getParameter("labscale") != null ) {
            labScale = request.getParameter("labscale");
        }
        if (request.getParameter("cql") != null) {
            cqlString = request.getParameter("cql");
        	try {
        		byte[] byteData = cqlString.getBytes("ISO_8859_1");
        		cqlString = new String(byteData, "UTF-8");
        	}
        	catch(Exception e){
        		System.out.println(e);
        	}
        }
        if (request.getParameter("viewparams") != null) {
        	viewParams = request.getParameter("viewparams");
        	try {
        		byte[] byteData = viewParams.getBytes("ISO_8859_1");
        		viewParams = new String(byteData, "UTF-8");
        	}
        	catch(Exception e){
        		System.out.println(e);
        	}
        }
        if (request.getParameter("numrange") != null) {
            numRange = Integer.parseInt(request.getParameter("numrange"));
        }
        if (request.getParameter("color") != null) {
            String theme = request.getParameter("color");

            try {
        		byte[] byteData = theme.getBytes("ISO_8859_1");
        		theme = new String(byteData, "UTF-8");
        	}
        	catch(Exception e){
        		System.out.println(e);
        	}
            
            if( this.colorMap.containsKey(theme) ) {
            	String[] colorVal = this.colorMap.get(theme);
            	colorFrom = Color.decode(colorVal[0]);
            	colorTo   = Color.decode(colorVal[1]);
            }
        }

        // ************************************
        // * Setting Range Values
        // ************************************
        ArrayList<Double> ranges = new ArrayList<Double>();
        
        ranges = wfsRangeList.createRangeList(
               this.geoserverURL,
                typeName, propName, cqlString, 
                viewParams, typeRange, numRange);

        // ************************************
        // * Setting Range Colors
        // ************************************
        ArrayList<String> colors = rangeColor.createRangeColors(
                colorFrom, colorTo, ranges.size() );
        
        // ************************************
        // * Creating the SLD
        // ************************************

        thematicSld sld = new thematicSld();
        StringBuffer sldBuff = sld.addHeader(typeName);

        int i;
        
        int ptRng  = (int) Math.ceil( (19-4)/(ranges.size()-1) );
        int ptSize = 5 + ptRng; //min size is 5
        
        for ( i = 0; i < (ranges.size() - 2); i++ ) {
        	if( geoType.compareToIgnoreCase("point") == 0 ) {
        		
        		sld.setPointSize( ptSize );
        		ptSize += ptRng;
        		
        		if(ptSize > 17)
        			ptSize = 17;
        	}
            sldBuff = sld.addRule( sldBuff, propName, 
                    ranges.get(i).doubleValue(), 
                    ranges.get(i + 1).doubleValue(), 
                    colors.get(i),false, geoType );
        }

        if( geoType.compareToIgnoreCase("point") == 0 ) {
        	if( ptSize < 17 ) 
        		ptSize = 17;
        	
    		sld.setPointSize( ptSize );
    	}
        sldBuff = sld.addRule( sldBuff, propName, 
                ranges.get(i).doubleValue(), 
                ranges.get(i + 1).doubleValue(), 
                colors.get(i),true, geoType );
        
        if( labScale.length() > 0 ) {
          sldBuff = sld.addLabelRule(sldBuff, labScale,propName);
        }
        sldBuff = sld.addFooter(sldBuff);

        // ************************************
        // * Writing to Session
        // ************************************
        String tSld = sldBuff.toString();

        HttpSession session = request.getSession(true);
        session.setAttribute( typeName, tSld );

        response.setContentType("text/text");
        response.getWriter().write(session.getId());

    }
}
