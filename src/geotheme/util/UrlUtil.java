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

package geotheme.util;

import javax.servlet.http.HttpServletRequest;

public class UrlUtil {

	public static String getUrl(HttpServletRequest req) { 
	    String scheme = req.getScheme();           // http 
	    String serverName = req.getServerName();   // hostname.com 
	    int serverPort = req.getServerPort();      // 8080 
	    String contextPath = req.getContextPath(); // /mywebapp 
	     
	    StringBuffer sb = new StringBuffer();
	    
	    sb.append(scheme);
	    sb.append("://").append(serverName);
	    sb.append(":").append(serverPort);
	    sb.append(contextPath);
	    
	    return sb.toString();
	}
}
