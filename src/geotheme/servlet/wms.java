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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import geotheme.bean.*;
import geotheme.pdf.*;


/**
 * Servlet implementation class wms
 */
public class wms extends HttpServlet {
    
	private static final long serialVersionUID = 1L;
    private String geoserverURL = new String();   
    private String pdfURL       = new String();
    private String pdfLayers    = new String();

    @Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();

		ResourceBundle rb = 
			ResourceBundle.getBundle("properties.thematic");
		
		String url = rb.getString("GEOSERVER.BASE.URL");
		
	    if( url.endsWith("/") ) {
            url = url.substring(0, url.length()-1);
        }
		
	    this.geoserverURL = url+"/wms";
	    this.pdfURL       = rb.getString("PDF.OSM.URL");
	    this.pdfLayers    = rb.getString("PDF.OSM.LAYERS");
	    
    }

    /**
     * @see HttpServlet#HttpServlet()
     */
    public wms() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#service(HttpServletRequest req, HttpServletResponse res)
     */
    protected void service(HttpServletRequest req, HttpServletResponse res) 
    throws ServletException, IOException {
    	
    	OutputStreamWriter wr = null;
        InputStream  in       = null;
        OutputStream out      = null;

        try {
            Map<String,Object> reqMap = new HashMap<String,Object>();
            
            Enumeration<?> en = req.getParameterNames();
            String key = new String();

            /**
             * Converting all Map Keys into Upper Case
             **/
            while(en.hasMoreElements()){
                key = (String)en.nextElement();
                reqMap.put(key.toUpperCase(), req.getParameter(key));
            }
            
            wmsParamBean wmsBean = new wmsParamBean();
            BeanUtils.populate(wmsBean, reqMap);

            HttpSession session = req.getSession(true);

            /**
             * Reading the saved SLD
             **/
            String sessionName = wmsBean.getLAYER();
            
            if( sessionName.length() < 1 )
            	sessionName = wmsBean.getLAYERS();

            if( session.getAttribute(sessionName) != null) {

                wmsBean.setSLD_BODY(
                       (String)session.getAttribute(
                               sessionName) );
                wmsBean.setSLD("");
                wmsBean.setSTYLES("");
            }
                        
            if( wmsBean.getREQUEST().compareToIgnoreCase("GetPDFGraphic") == 0 ) {
                /**
                 * Generate PDF Request
                 */
                generatePDF pdf = new generatePDF(this.pdfURL,
                        this.pdfLayers);
                
                ByteArrayOutputStream baos = pdf.createPDFFromImage(
                        wmsBean, this.geoserverURL);
                
                res.addHeader("Content-Type", "application/force-download"); 
                res.addHeader("Content-Disposition", 
                        "attachment; filename=\"MapOutput.pdf\"");
                
                res.getOutputStream().write(baos.toByteArray());
            }
            else {
                /**
                 * Generating Map from GeoServer
                 **/
                URL geoURL = new URL(this.geoserverURL);

                URLConnection geoConn = geoURL.openConnection();
                geoConn.setDoOutput(true);

                wr = new OutputStreamWriter(geoConn.getOutputStream(),"UTF-8");
                wr.write( wmsBean.getURL_PARAM() );
                wr.flush();

                in  = geoConn.getInputStream();
                out = res.getOutputStream();

                res.setContentType(wmsBean.getFORMAT());

                int b;         
                while((b = in.read()) != -1 ) {
                    out.write(b);
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        finally {
        	if( out != null ) {
        		out.flush();
        		out.close();
        	}
        	if( in != null ) {
        		in.close();
        	}
        	if( wr != null ) {
        		wr.close();
        	}
        }
    }
}
