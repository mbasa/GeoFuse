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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

import javax.imageio.ImageIO;
  
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import geotheme.bean.*;

public class generatePDF {

    private String pdfURL       = new String();
    private String pdfLayers    = new String();

    public generatePDF(String url, String layers) {
        this.pdfURL    = url;
        this.pdfLayers = layers;
    }
    
    public ByteArrayOutputStream createPDFFromImage( 
            wmsParamBean wpb, String host ) 
    throws  IOException, COSVisitorException
    {
        PDDocument doc = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        OutputStreamWriter wr = null;
        OutputStreamWriter wl = null;
        URLConnection geoConn = null;
        URLConnection legConn = null;
        
        int width  = 500;
        int height = 500;
       
        wpb.setBBOX(retainAspectRatio(wpb.getBBOX()));
        
        StringBuffer sb = new StringBuffer();
        sb.append(this.pdfURL);
        sb.append("&layers=").append(this.pdfLayers);
        sb.append("&bbox=").append(wpb.getBBOX());
        sb.append("&Format=image/jpeg");
        sb.append("&width=").append(width);
        sb.append("&height=").append(height);

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
            
            wr = new OutputStreamWriter(geoConn.getOutputStream(),"UTF-8");
            wr.write( wpb.getURL_PARAM() );

            wr.flush();                        
              
            wpb.setREQUEST("GetLegendGraphic");
            wpb.setTRANSPARENT("FALSE");
            wpb.setWIDTH ("");
            wpb.setHEIGHT("");
            
            wl = new OutputStreamWriter(legConn.getOutputStream(),"UTF-8");
            wl.write( wpb.getURL_PARAM() + "&legend_options=fontSize:9;" );
            wl.flush();
            
            doc = new PDDocument();
 
            PDPage page = new PDPage(/*PDPage.PAGE_SIZE_A4*/);
            doc.addPage( page );

            BufferedImage img = ImageIO.read( geoConn.getInputStream() );
            BufferedImage leg = ImageIO.read( legConn.getInputStream() );
            
            PDXObjectImage ximage  = new PDPixelMap(doc,img);
            PDXObjectImage xlegend = new PDPixelMap(doc,leg);
            PDXObjectImage xosm   = new PDJpeg(doc, osm.openStream() );
            
            PDPageContentStream contentStream = 
                    new PDPageContentStream(doc, page);            
            
            PDFont font = PDType1Font.HELVETICA_OBLIQUE;
            
            contentStream.beginText();
            contentStream.setFont(font, 8);
            contentStream.moveTextPositionByAmount(450, 10);
            Date date = new Date();
            contentStream.drawString(date.toString());
            contentStream.endText();  
            
            contentStream.beginText();
            contentStream.setFont(font, 8);
            contentStream.moveTextPositionByAmount(10, 10);
            contentStream.drawString("GeoFuse Report: mario.basa@gmail.com");
            contentStream.endText();
          
            contentStream.drawImage( xosm  , 20, 160 );
            contentStream.drawImage( ximage, 20, 160 );
            contentStream.drawImage(xlegend, 
                    width-xlegend.getWidth()-3, 170 );
          
            contentStream.beginText();
            contentStream.setFont(font, 50);
            contentStream.moveTextPositionByAmount(20, 720);
            contentStream.drawString(wpb.getPDF_TITLE());
            contentStream.endText();
            
            contentStream.beginText();
            contentStream.setFont(font, 18);
            contentStream.moveTextPositionByAmount(20, 695);
            contentStream.drawString(wpb.getPDF_NOTE());
            contentStream.endText();
            
            contentStream.setStrokingColor(180, 180, 180);
            
            float bx[] = {10f ,10f ,30+width,30+width,10f};
            float by[] = {150f,170+height,170+height,150f,150f};
            contentStream.drawPolygon(bx, by);

            contentStream.close();
            
            doc.save( baos );
            
        }
        catch(Exception e ) {
            e.printStackTrace();
        }
        finally
        {
            if( doc != null ) {
                doc.close();
            }
            
            if( wr != null ) {
                wr.close();
            }
            
            if( wl != null ) {
                wl.close();
            }
        }
        
        return baos;
    }

    public String retainAspectRatio(String bbox) {
        
        String arr[] = bbox.split(",");
        
        float x1 = Float.parseFloat(arr[0]);
        float y1 = Float.parseFloat(arr[1]);
        float x2 = Float.parseFloat(arr[2]);
        float y2 = Float.parseFloat(arr[3]);
        
        float width  = x2 - x1;
        float height = y2 - y1;
        
        float centerx = x1 + (width/2);
        float centery = y1 + (height/2);
        
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
        
        StringBuffer sb = new StringBuffer();
        sb.append(x1).append(",");
        sb.append(y1).append(",");
        sb.append(x2).append(",");
        sb.append(y2);

        return sb.toString();
    }
}
