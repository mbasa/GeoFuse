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

package geotheme.sld;

import java.awt.Color;
import java.util.*;

import geotheme.wfsUtil.wfsRangeList;
import geotheme.color.rangeColor;
import geotheme.sld.thematicSld;

/**
 * Servlet implementation class gsld
 */
public class generateSLD {

	private String geoserverURL = new String();
	private Map<String,String[]> colorMap = new HashMap<String,String[]>();
	
	private Color  colorFrom  = Color.YELLOW;
	private Color  colorTo    = Color.RED;
	private String color      = new String();
	private String typeName   = new String();//geoserver layer name
	private String geoType    = new String();//point,line,polygon
	private String propName   = new String();//layer attribute column name
	private String typeRange  = "EQRange";
	private String cqlString  = "";
	private String viewParams = "";
	private String labScale   = "";
	private int    numRange   = 6;
	
	private ArrayList<Double> rangeValuesList;
	private ArrayList<String> rangeColorsList;
	
	public void initColorMap() {
		
		ResourceBundle rb = 
			ResourceBundle.getBundle("properties.thematic");
		
		this.geoserverURL = rb.getString("GEOSERVER.BASE.URL");
				
		String[] colorNames= rb.getString("THEMATIC.COLOR.NAMES").split(",");
		String[] colorVals = rb.getString("THEMATIC.COLORS").split(",");

		this.setColorMap(colorNames,colorVals);		
	}

    /**
     * 
     */
    public generateSLD() {
    	this.initColorMap();
    }

    /**
     * 
     * @param geoserver
     * @param colorNames
     * @param colorVals
     */
    public generateSLD(String geoserver,String colorNames,String colorVals) {
    	
    	this.setGeoserverURL(geoserver);
    	this.setColorMap(colorNames.split(","),colorVals.split(","));
    }
    
    /**
     * 
     * @return String
     */
    public String getSLD() {
    	return this.getSLD(null,null);
    }

    /**
     * 
     * @param mRanges
     * @param mColors
     * @return String
     */
    public String getSLD(ArrayList<Double> mRanges,ArrayList<String> mColors) {
    	
    	StringBuffer sldBuff = new StringBuffer();

    	ArrayList<Double> ranges;
    	ArrayList<String> colors;

    	try {
    		// ************************************
    		// * Setting Range Values
    		// ************************************
    		if( mRanges != null ) {
    			ranges = mRanges;
    		}
    		else {
    			ranges = wfsRangeList.createRangeList(
    					this.geoserverURL,
    					this.typeName  , this.propName, this.cqlString, 
    					this.viewParams, this.typeRange,this.numRange );
    		}
    		this.setRangeValuesList(ranges);
    		// ************************************
    		// * Setting Range Colors
    		// ************************************
    		if( mColors != null ) {
    			colors = mColors;
    		}
    		else {
    			colors = rangeColor.createRangeColors(
    					colorFrom, colorTo, ranges.size() );
    		}
    		this.setRangeColorsList(colors);
    		// ************************************
    		// * Creating the SLD
    		// ************************************

    		thematicSld sld = new thematicSld();
    		sldBuff = sld.addHeader(typeName);

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

    	}
    	catch( Exception ex ) {
    		ex.printStackTrace();
    	}

    	return sldBuff.toString();

        // ************************************
        // * Writing to Session
        // ************************************
        /*String tSld = sldBuff.toString();

        HttpSession session = request.getSession(true);
        session.setAttribute( typeName, tSld );

        response.setContentType("text/text");
        response.getWriter().write(session.getId());
         */
    }

	/**
	 * @return the geoserverURL
	 */
	public String getGeoserverURL() {
		return geoserverURL;
	}

	/**
	 * @param geoserverURL the geoserverURL to set
	 */
	public void setGeoserverURL(String geoserverURL) {
		this.geoserverURL = geoserverURL;
	}

	/**
	 * @return the colorMap
	 */
	public Map<String, String[]> getColorMap() {
		return colorMap;
	}

	/**
	 * @param colorMap the colorMap to set
	 */
	public void setColorMap(Map<String, String[]> colorMap) {
		this.colorMap = colorMap;
	}

	/**
	 * 
	 * @param colorNames[]
	 * @param colorVals[]
	 */
	public void setColorMap(String[] colorNames, String[] colorVals) {
		
		this.colorMap = new HashMap<String,String[]>();
		
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
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
		
		if( this.colorMap.containsKey(color) ) {
        	String[] colorVal = this.colorMap.get(color);
        	this.colorFrom    = Color.decode(colorVal[0]);
        	this.colorTo      = Color.decode(colorVal[1]);
        }
	}
	public void setColor(String fromColor,String toColor) {
		this.colorFrom  = Color.decode(fromColor);
		this.colorTo    = Color.decode(toColor);
	}
	/**
	 * @return the colorFrom
	 */
	public Color getColorFrom() {
		return colorFrom;
	}

	/**
	 * @param colorFrom the colorFrom to set
	 */
	public void setColorFrom(Color colorFrom) {
		this.colorFrom = colorFrom;
	}

	/**
	 * @return the colorTo
	 */
	public Color getColorTo() {
		return colorTo;
	}

	/**
	 * @param colorTo the colorTo to set
	 */
	public void setColorTo(Color colorTo) {
		this.colorTo = colorTo;
	}

	/**
	 * @return the typeName
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @param typeName the typeName to set
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * @return the geoType
	 */
	public String getGeoType() {
		return geoType;
	}

	/**
	 * @param geoType the geoType to set
	 */
	public void setGeoType(String geoType) {
		this.geoType = geoType;
	}

	/**
	 * @return the propName
	 */
	public String getPropName() {
		return propName;
	}

	/**
	 * @param propName the propName to set
	 */
	public void setPropName(String propName) {
		this.propName = propName;
	}

	/**
	 * @return the typeRange
	 */
	public String getTypeRange() {
		return typeRange;
	}

	/**
	 * @param typeRange the typeRange to set
	 */
	public void setTypeRange(String typeRange) {
		this.typeRange = typeRange;
	}

	/**
	 * @return the cqlString
	 */
	public String getCqlString() {
		return cqlString;
	}

	/**
	 * @param cqlString the cqlString to set
	 */
	public void setCqlString(String cqlString) {
		this.cqlString = cqlString;
	}

	/**
	 * @return the viewParams
	 */
	public String getViewParams() {
		return viewParams;
	}

	/**
	 * @param viewParams the viewParams to set
	 */
	public void setViewParams(String viewParams) {
		this.viewParams = viewParams;
	}

	/**
	 * @return the labScale
	 */
	public String getLabScale() {
		return labScale;
	}

	/**
	 * @param labScale the labScale to set
	 */
	public void setLabScale(String labScale) {
		this.labScale = labScale;
	}

	/**
	 * @return the numRange
	 */
	public int getNumRange() {
		return numRange;
	}

	/**
	 * @param numRange the numRange to set
	 */
	public void setNumRange(int numRange) {
		this.numRange = numRange;
	}

	/**
	 * @return the rangeValuesList
	 */
	public ArrayList<Double> getRangeValuesList() {
		return rangeValuesList;
	}

	/**
	 * @param rangeValuesList the rangeValuesList to set
	 */
	public void setRangeValuesList(ArrayList<Double> rangeValuesList) {
		this.rangeValuesList = rangeValuesList;
	}

	/**
	 * @return the rangeColorsList
	 */
	public ArrayList<String> getRangeColorsList() {
		return rangeColorsList;
	}

	/**
	 * @param rangeColorsList the rangeColorsList to set
	 */
	public void setRangeColorsList(ArrayList<String> rangeColorsList) {
		this.rangeColorsList = rangeColorsList;
	}
}
