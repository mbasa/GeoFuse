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

package geotheme.pdf;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pdfjet.*;

import geotheme.bean.*;
import geotheme.db.projectionSetQuery;

public class generatePDF {

    private String pdfURL       = new String();
    private String pdfLayers    = new String();

    public generatePDF(String url, String layers) {
        this.pdfURL    = url;
        this.pdfLayers = layers;
    }
    
    public ByteArrayOutputStream createPDFFromImage( 
            wmsParamBean wpb, String host ) throws  Exception
    {
        Logger LOGGER = LogManager.getLogger();
        
    	/**
    	 * re-projecting to epsg:3857
    	 */
    	wpb.setBBOX( this.projectTo3857(wpb.getBBOX(), wpb.getSRS()));
    	wpb.setSRS("EPSG:3857");
    	
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        PDF pdf   = new PDF(output);
        Page page = new Page(pdf, A4.PORTRAIT);
        
        Font f1   = new Font(pdf, "KozMinProVI-Bold",CodePage.UNICODE);
        Font f2   = new Font(pdf, CoreFont.HELVETICA);
                
        OutputStreamWriter wr = null;
        OutputStreamWriter wl = null;
        URLConnection geoConn = null;
        URLConnection legConn = null;
        
        int width  = 520;
        int height = 520;
       
        wpb.setBBOX(retainAspectRatio(wpb.getBBOX()));
        
        StringBuffer sb = new StringBuffer();
        sb.append(this.pdfURL);
        sb.append("&LAYERS=").append(this.pdfLayers);
        sb.append("&BBOX=").append(wpb.getBBOX());
        sb.append("&FORMAT=image/jpeg");
        sb.append("&WIDTH=").append(width);
        sb.append("&HEIGHT=").append(height);

        sb.append("&SERVICE=WMS&VERSION=1.1.1&REQUEST=GetMap&STYLES=&SRS=");
        sb.append(wpb.getSRS());

        LOGGER.debug("PDF Request URL: {}", sb.toString() );
        
        try
        {
            wpb.setREQUEST("GetMap");
            wpb.setWIDTH(Integer.toString(width));
            wpb.setHEIGHT(Integer.toString(height));
            
            URL url  = new URL( host );
            URL urll = new URL( host );
            URL osm  = new URL( sb.toString() );        

            geoConn = url.openConnection();
            geoConn.setDoOutput(true);
           
            legConn = urll.openConnection();
            legConn.setDoOutput(true);
            
            wr = new OutputStreamWriter(
                    geoConn.getOutputStream(),"UTF-8");
            wr.write( wpb.getURL_PARAM() );

            wr.flush();                        
              
            wpb.setREQUEST("GetLegendGraphic");
            wpb.setTRANSPARENT("FALSE");
            wpb.setWIDTH ("");
            wpb.setHEIGHT("");
            
            if( wpb.getLAYERS() != "" && wpb.getLAYER() == "" ) {
                wpb.setLAYER(wpb.getLAYERS());
                wpb.setLAYERS("");
            }
            
            wl = new OutputStreamWriter(legConn.getOutputStream(),"UTF-8");
            wl.write( wpb.getURL_PARAM() + "&legend_options=fontSize:9;" );
            wl.flush();
            
            //::::::::::::::::::::::::::
            //: Drawing the Maps
            //::::::::::::::::::::::::::

            BufferedInputStream map_bis = 
                    new BufferedInputStream(geoConn.getInputStream());
            BufferedInputStream leg_bis = 
                    new BufferedInputStream(legConn.getInputStream());
            BufferedInputStream osm_bis = 
                    new BufferedInputStream(osm.openStream());
            
            Image img0 = new Image(pdf,osm_bis,ImageType.JPEG);
            img0.setPosition(30d, 180d);
            img0.drawOn(page);
            
            Image img1 = new Image(pdf,map_bis,ImageType.PNG);
            img1.setPosition(30d, 180d);
            img1.drawOn(page);
            
            Image img2 = new Image(pdf,leg_bis,ImageType.PNG);
            
            if(img2.getHeight() > 50 && img2.getWidth() > 50) {
                double x_width  = (30d  + width ) - img2.getWidth() - 5d;
                double x_height = (180d + height) - img2.getHeight()- 5d;
                
                img2.setPosition(x_width, x_height);
                img2.drawOn(page);
            }
            
            //::::::::::::::::::::::::::
            //: Drawing the Box Decors
            //::::::::::::::::::::::::::
            
            page.setPenWidth(0.9);
            page.drawRect(30.0, 180.0, width, height);
            page.setPenWidth(1.5);
            page.drawRect(25.0, 175.0, width+10, height+10);
            
            //::::::::::::::::::::::::::
            //: Drawing the Annotations
            //::::::::::::::::::::::::::
            
            f1.setSize(38.0);
            f2.setSize(38.0);
            TextLine text = new TextLine(f1);
            
            if(wpb.getPDF_TITLE().matches("\\A\\p{ASCII}*\\z"))
                text.setFont(f2);
            
            text.setText(wpb.getPDF_TITLE());
            text.setPosition(30, 80);
            text.setColor(RGB.DARK_GRAY);
            text.drawOn(page);
            
            f1.setSize(14d);
            f2.setSize(14d);
            
            if(wpb.getPDF_NOTE().matches("\\A\\p{ASCII}*\\z"))
                text.setFont(f2);
            else
                text.setFont(f1);
            
            text.setColor(RGB.GRAY);
            text.setText(wpb.getPDF_NOTE());
            text.setPosition(30, 110);            
            text.drawOn(page);
            
            f2.setSize(10d);
            text.setFont(f2);
            text.setColor(RGB.GRAY);
            text.setText("GeoFuse Report: mario.basa@gmail.com");
            text.setPosition(30d, 800d);
            text.drawOn(page);
            
            Date date = new Date();
            text.setText( date.toString() );
            text.setPosition(400d, 800d);
            text.drawOn(page);
            
            pdf.flush();            
        }
        catch(Exception e ) {
            e.printStackTrace();
        }
        finally
        {
            if( wr != null ) {
                wr.close();
            }
            
            if( wl != null ) {
                wl.close();
            }
        }
        
        return output;
    }

    public String retainAspectRatio(String bbox) {
        
        String arr[] = bbox.split(",");
        
        double x1 = Double.parseDouble(arr[0]);
        double y1 = Double.parseDouble(arr[1]);
        double x2 = Double.parseDouble(arr[2]);
        double y2 = Double.parseDouble(arr[3]);
        
        double width  = x2 - x1;
        double height = y2 - y1;
        
        double centerx = x1 + (width/2);
        double centery = y1 + (height/2);
        
        if( width > height ) {
            x1 = centerx - (width/2);
            y1 = centery - (width/2);
            x2 = centerx + (width/2);
            y2 = centery + (width/2);
        }
        else if( width < height ) {
            x1 = centerx - (height/2);
            y1 = centery - (height/2);
            x2 = centerx + (height/2);
            y2 = centery + (height/2);
        }
        
		DecimalFormat df = new DecimalFormat("##########.##########");
		
        StringBuffer sb = new StringBuffer();
        sb.append(df.format(x1)).append(",");
        sb.append(df.format(y1)).append(",");
        sb.append(df.format(x2)).append(",");
        sb.append(df.format(y2));

        return sb.toString();
    }
    
    public String toHex(String str) {

        StringBuffer ostr = new StringBuffer();

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            
            if ((ch >= 0x0020) && (ch <= 0x007e)) {
                ostr.append(ch);
            } else {
                ostr.append("\\u");
                String hex = Integer.toHexString(str.charAt(i) & 0xFFFF);
                
                for (int j = 0; j < 4 - hex.length(); j++)
                    ostr.append("0");
                
                ostr.append(hex.toLowerCase());
                // ostr.append(hex.toLowerCase(Locale.ENGLISH));
            }
        }
        return (new String(ostr));
    }
    
    public String projectTo3857(String bbox,String srs) {
    	
    	String coords[] = bbox.split(",");
    	int epsg        = Integer.parseInt( srs.split(":")[1] );
    	
    	double firstPt[] = projectionSetQuery.changeProjection(
    			Double.parseDouble(coords[0]), Double.parseDouble(coords[1]), 
    			epsg, 3857);
    	
    	double endPt[]  = projectionSetQuery.changeProjection(
    			Double.parseDouble(coords[2]), Double.parseDouble(coords[3]), 
    			epsg, 3857);
    	
		DecimalFormat df = new DecimalFormat("##########.##########");
		
    	StringBuffer sb = new StringBuffer();
    	sb.append( df.format( firstPt[0] ) ).append(",");
    	sb.append( df.format( firstPt[1] ) ).append(",");
    	sb.append( df.format( endPt[0]) ).append(",");
    	sb.append( df.format( endPt[1]) );

    	return sb.toString();
    }
}
