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
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import geotheme.csv.processCSV;
import geotheme.util.UrlUtil;

/**
 * Servlet implementation class inputData
 */
public class inputData extends HttpServlet {
	
    private static final long serialVersionUID = 1L;
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public inputData() {
        super();        
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, 
	 * HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, 
			HttpServletResponse response) 
	throws ServletException, IOException {
		
	    //request.setCharacterEncoding("UTF-8")
	    
	    Logger LOGGER      = LogManager.getLogger();	    
		String inputString = request.getParameter("data");
		String layerName   = request.getParameter("layername");
		
		LOGGER.debug( "Input LayerName = {}",layerName );
		
        if (inputString == null || inputString == "") {
            response.setContentType("text/text");
            PrintWriter out = response.getWriter();
            out.write("Error: input data is null or blank ");
            out.close();

            return;
        }

        if (request.getCharacterEncoding().equalsIgnoreCase("ISO-8859-1")) {
		    byte[] bytes = inputString.getBytes("ISO-8859-1");
		    inputString  = new String(bytes, "UTF-8");
		}
		
		processCSV pcsv = new processCSV();
		String result   = pcsv.process(inputString, layerName);
		
		response.setContentType("text/text");
		PrintWriter out = response.getWriter();

		if( result.startsWith("Error") ) {
		    out.write( result );
		    out.close();
		}
		else {
		    StringBuffer sb = new StringBuffer();

            sb.append( UrlUtil.getUrl( request ) );
            sb.append("/ui/showtheme?layer=");
            sb.append(result);
            
            out.write( sb.toString() );
            out.close();
		}
		
		return;
	}

}
