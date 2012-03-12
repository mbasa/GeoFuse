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

package geotheme.wfsUtil;

import java.io.*;
import java.net.*;
import java.util.*;

import org.jdom.*;
import org.jdom.input.*;

public class wfsParse {

    private String urlString = null;
    private String layerType = "polygon";
    private String[] bnd     = {"EPSG:4326","-180","-90","180","90"};
    private ArrayList<String> prop  = new ArrayList<String>();

    public wfsParse(String url) {

        setUrlString(url);
    }

    public wfsParse() {
        ;
    }

    public void setUrlString(String url) {
        if( url.endsWith("/") ) {
            url = url.substring(0, url.length()-1);
        }
        
        this.urlString = url;
    }

    public ArrayList<Double> parsePropertyVal(
            String layerName,
            String fieldName,
            String cqlQuery ,
            String viewParams) {
        
        ArrayList<Double> retDo = new ArrayList<Double>();
        
        StringBuffer sb = new StringBuffer();
        
        sb.append(this.urlString);
        sb.append("/wfs?service=wfs&request=GetFeature&version=1.0.0");
        sb.append("&typeName=").append(layerName);
        
        if(fieldName != null) {
        	sb.append("&propertyName=");
        	try {
        		sb.append(URLEncoder.encode(fieldName, "UTF-8"));
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }
        
        if (cqlQuery != null && cqlQuery.length() > 2) {
            sb.append("&CQL_FILTER=");
            try {
                sb.append(URLEncoder.encode(cqlQuery, "UTF-8"));
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if(viewParams != null && viewParams.length() > 2 ) {
        	sb.append("&VIEWPARAMS=");
        	try {
                sb.append(URLEncoder.encode(viewParams, "UTF-8"));
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        try {
            URL url = new URL( sb.toString() );
            URLConnection conn = url.openConnection();

            Document doc = new SAXBuilder().build(
                    new InputStreamReader(conn.getInputStream(),"UTF-8"));

            List<?> recs = doc.getRootElement().getChildren();

            if (recs != null) {
                Iterator<?> i = recs.iterator();

                while (i.hasNext()) {
                    Element elem = (Element) i.next();

                    if (elem.getName().compareToIgnoreCase(
                            "featureMember") == 0) {
                        
                        Element data = 
                            (Element) elem.getChildren().get(0);
                        
                        retDo.add( Double.valueOf(
                            data.getChildText(fieldName, 
                                        data.getNamespace()) ) ) ;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return retDo;
    }

    public String[] parseBnd(String layerName) {
        
        String[] retSt  = {"EPSG:4326","-180","-90","180","90"};
        StringBuffer sb = new StringBuffer();
        
        sb.append(this.urlString);
        sb.append("/wfs?service=wfs&request=GetCapabilities&version=1.0.0");   

        try {
            URL url = new URL(sb.toString());
            URLConnection conn = url.openConnection();

            Document doc = new SAXBuilder().build(
                    new InputStreamReader(conn.getInputStream(),"UTF-8"));

            Namespace ns = doc.getRootElement().getNamespace();

            Element featList = doc.getRootElement().getChild(
                    "FeatureTypeList", ns);

            if (featList == null) {
                System.out.println("no FeatureTypeList");
                return retSt;
            }

            List<?> featType = featList.getChildren("FeatureType", ns);
            Iterator<?> i = featType.iterator();

            String featName = new String();

            while (i.hasNext()) {
                Element elem = (Element) i.next();

                featName = elem.getChildText("Name", ns);

                if (featName.compareToIgnoreCase( layerName ) == 0) {

                    Element bnd = elem.getChild(
                            "LatLongBoundingBox", ns);
                    
                    //:::::::::::::::::::::::::::::::
                    //:: Geoserver always makes the 
                    //:: LatLongBoundingBox to WGS84
                    //:::::::::::::::::::::::::::::::
                    //retSt[0] = elem.getChildText("SRS", ns);
                    retSt[0] = "EPSG:4326";
                    retSt[1] = bnd.getAttributeValue("minx");
                    retSt[2] = bnd.getAttributeValue("miny");
                    retSt[3] = bnd.getAttributeValue("maxx");
                    retSt[4] = bnd.getAttributeValue("maxy");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return retSt;
    }

    public void parsePropAndBnd(String layerName) {
       this.setProp( this.parseProperty(layerName) );
       this.setBnd ( this.parseBnd(layerName) );
    }
    
    public ArrayList<String> parseProperty(String layerName) {
        
        ArrayList<String> retSt  = new ArrayList<String>();
        
        StringBuffer sb = new StringBuffer();
        
        sb.append(this.urlString);
        sb.append("/wfs?request=DescribeFeatureType&version=1.1.1&typeName=");
        sb.append(layerName);
        sb.append("&service=wfs");
        
        try {
            URL url = new URL( sb.toString() );
            URLConnection conn = url.openConnection();

            Document doc = new SAXBuilder().build(
                    new InputStreamReader(conn.getInputStream(),"UTF-8"));

            Namespace ns = doc.getRootElement().getNamespace();

            Element sequence = doc.getRootElement().
                getChild("complexType",    ns). 
                getChild("complexContent", ns).
                getChild("extension",      ns).
                getChild("sequence",       ns);

            if ( sequence == null) {
                System.out.println("no FeatureTypeList");
                return retSt;
            }

            List<?> seqElement = sequence.getChildren("element", ns);
            Iterator<?> i = seqElement.iterator();
            
            String propName = new String();
            String propType = new String();
            
            while (i.hasNext()) {
                Element elem = (Element) i.next();

                propName = elem.getAttributeValue("name");
                propType = elem.getAttributeValue("type");

                if(propName.compareToIgnoreCase("the_geom") == 0 ) {
                    if( propType.compareToIgnoreCase( 
                            "gml:MultiSurfacePropertyType" ) == 0 )
                        this.setLayerType("polygon");
                    
                    if( propType.compareToIgnoreCase( 
                            "gml:PointPropertyType" ) == 0 )
                        this.setLayerType("point");
                   
                    if( propType.compareToIgnoreCase( 
                            "gml:MultiLineStringPropertyType" ) == 0 )
                        this.setLayerType("line");
                }
                
                if(propName.compareToIgnoreCase("the_geom") != 0 &&
                   propName.endsWith("-ID") == false &&
                   propType.compareToIgnoreCase("xsd:string") != 0)
                retSt.add(propName);            
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return retSt;
    }

    public String getLayerType() {
        return layerType;
    }

    public void setLayerType(String layerType) {
        this.layerType = layerType;
    }

    public String[] getBnd() {
        return bnd;
    }

    public void setBnd(String[] bnd) {
        this.bnd = bnd;
    }

    public ArrayList<String> getProp() {
        return prop;
    }

    public void setProp(ArrayList<String> prop) {
        this.prop = prop;
    }
}
