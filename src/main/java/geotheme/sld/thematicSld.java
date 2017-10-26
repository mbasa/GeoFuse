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

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class thematicSld {

	private int pointSize = 16;
	
    public StringBuffer addLabelRule(StringBuffer sb,
            String labelScale,String propertyName ) {
    
        sb.append("<sld:Rule>");
        
        sb.append("<sld:MaxScaleDenominator>");
        sb.append(labelScale);
        sb.append("</sld:MaxScaleDenominator>");

        sb.append("<sld:TextSymbolizer>");
        sb.append("<sld:Label>");
        sb.append("<ogc:PropertyName>");
        sb.append(propertyName);
        sb.append("</ogc:PropertyName>");
        sb.append("</sld:Label>");
        
        sb.append("<sld:Font>");
        sb.append("<sld:CssParameter name=\"font-family\">");
        sb.append("Times New Roman");
        sb.append("</sld:CssParameter>");
        sb.append("<sld:CssParameter name=\"font-style\">");
        sb.append("Normal");
        sb.append("</sld:CssParameter>");
        sb.append("<sld:CssParameter name=\"font-size\">");
        sb.append(10);
        sb.append("</sld:CssParameter>");
        sb.append("<sld:CssParameter name=\"font-weight\">");
        sb.append("Normal");
        sb.append("</sld:CssParameter>");
        sb.append("</sld:Font>");

        sb.append("<sld:Halo>");
        sb.append("<sld:Radius>");
        sb.append("<ogc:Literal>");
        sb.append(2);
        sb.append("</ogc:Literal>");
        sb.append("</sld:Radius>");
        sb.append("<sld:Fill>");
        sb.append("<sld:CssParameter name=\"fill\">");
        sb.append("#FFFFFF");
        sb.append("</sld:CssParameter>");
        sb.append("<sld:CssParameter name=\"fill-opacity\">");
        sb.append(0.85);
        sb.append("</sld:CssParameter>");               
        sb.append("</sld:Fill>");
        sb.append("</sld:Halo>");

        sb.append("<sld:Fill>");
        sb.append("<sld:CssParameter name=\"fill\">");
        sb.append("#FF0000");
        sb.append("</sld:CssParameter>");
        sb.append("</sld:Fill>");

        sb.append("</sld:TextSymbolizer>");
        sb.append("</sld:Rule>");
        return sb;
    }
    
    public StringBuffer addRule(StringBuffer sb, String propertyName,
            double rangeFrom, double rangeTo, String color,
            boolean isLast, String geoType ) {

        sb.append("<sld:Rule>");
        sb.append("<sld:Name>default</sld:Name>");

        NumberFormat formatter = new DecimalFormat("###,###,###.###");

        if (rangeTo < rangeFrom) {
            sb.append("<sld:Title>");
            sb.append(formatter.format(rangeFrom));
            sb.append(" ~</sld:Title>");

            sb.append("<ogc:Filter>");
            sb.append("<ogc:PropertyIsGreaterThan>");

            sb.append("<ogc:PropertyName>");
            sb.append(propertyName);
            sb.append("</ogc:PropertyName>");

            sb.append("<ogc:Literal>");
            sb.append(rangeFrom);
            sb.append("</ogc:Literal>");

            sb.append("</ogc:PropertyIsGreaterThan>");
            sb.append("</ogc:Filter>");
        } else {
            sb.append("<sld:Title>");
            sb.append(formatter.format(rangeFrom)).append(" ~ ");
            sb.append(formatter.format(rangeTo));
            sb.append("</sld:Title>");

            sb.append("<ogc:Filter>");
            sb.append("<ogc:And>");

            sb.append("<ogc:PropertyIsGreaterThanOrEqualTo>");
            sb.append("<ogc:PropertyName>");
            sb.append(propertyName);
            sb.append("</ogc:PropertyName>");
            sb.append("<ogc:Literal>");
            sb.append(rangeFrom);
            sb.append("</ogc:Literal>");
            sb.append("</ogc:PropertyIsGreaterThanOrEqualTo>");

            if( isLast )
                sb.append("<ogc:PropertyIsLessThanOrEqualTo>");
            else
                sb.append("<ogc:PropertyIsLessThan>");
            sb.append("<ogc:PropertyName>");
            sb.append(propertyName);
            sb.append("</ogc:PropertyName>");
            sb.append("<ogc:Literal>");
            sb.append(rangeTo);
            sb.append("</ogc:Literal>");
            if( isLast )
                sb.append("</ogc:PropertyIsLessThanOrEqualTo>");
            else
                sb.append("</ogc:PropertyIsLessThan>");

            sb.append("</ogc:And>");
            sb.append("</ogc:Filter>");

        }

        if( geoType.compareToIgnoreCase("polygon") == 0 ) {
            sb = polygonSymbolizer(color,sb);
        }
        else if( geoType.compareToIgnoreCase("point") == 0 ) {
            sb = pointSymbolizer(color,sb);
        }
        else if( geoType.compareToIgnoreCase("line") == 0 ) {
            sb = lineSymbolizer(color,sb);
        }

        sb.append("</sld:Rule>");

        return sb;
    }

    public StringBuffer lineSymbolizer(String color,StringBuffer sb) {
        
        sb.append("<sld:LineSymbolizer>");
        sb.append("<sld:Stroke>");
        
        sb.append("<sld:CssParameter name=\"stroke\">");
        sb.append("<ogc:Literal>");
        sb.append(color);
        sb.append("</ogc:Literal>");
        sb.append("</sld:CssParameter>");
        
        sb.append("<sld:CssParameter name=\"stroke-width\">");
        sb.append("<ogc:Literal>");
        sb.append("6");
        sb.append("</ogc:Literal>");
        sb.append("</sld:CssParameter>");
        
        sb.append("</sld:Stroke>");
        sb.append("</sld:LineSymbolizer>");

        return sb;
    }
    
    public StringBuffer pointSymbolizer(String color,StringBuffer sb) {
        sb.append("<sld:PointSymbolizer>");
        sb.append("<sld:Graphic>");
        sb.append("<sld:Mark>");
        sb.append("<sld:WellKnownName>");
        sb.append("<ogc:Literal>circle</ogc:Literal>");
        sb.append("</sld:WellKnownName>");
        
        sb.append("<sld:Fill>");
        sb.append("<sld:CssParameter name=\"fill\">");
        sb.append("<ogc:Literal>");
        sb.append(color);
        sb.append("</ogc:Literal>");
        sb.append("</sld:CssParameter>");
        sb.append("</sld:Fill>");
        
        sb.append("<sld:Stroke>");
        sb.append("<sld:CssParameter name=\"stoke\">");
        sb.append("<ogc:Literal>#777777</ogc:Literal>");
        sb.append("</sld:CssParameter>");
        sb.append("</sld:Stroke>");

        sb.append("</sld:Mark>");
        
        sb.append("<sld:size>");
        sb.append("<ogc:Literal>").append(this.pointSize);
        sb.append("</ogc:Literal>");
        sb.append("</sld:size>");
        
        sb.append("</sld:Graphic>");
        sb.append("</sld:PointSymbolizer>");
        
        return sb;
    }

    public StringBuffer polygonSymbolizer(String color,StringBuffer sb) {
        
        sb.append("<sld:PolygonSymbolizer>");
        sb.append("<sld:Fill>");
        sb.append("<sld:CssParameter name=\"fill\">");

        sb.append("<ogc:Literal>");
        sb.append(color);
        sb.append("</ogc:Literal>");

        sb.append("</sld:CssParameter>");
        sb.append("<sld:CssParameter name=\"fill-opacity\">");
        sb.append("<ogc:Literal>0.6</ogc:Literal>");
        sb.append("</sld:CssParameter>");
        sb.append("</sld:Fill>");
        sb.append("<sld:Stroke>");
        sb.append("<sld:CssParameter name=\"stroke\">");
        sb.append("<ogc:Literal>#444444</ogc:Literal>");
        sb.append("</sld:CssParameter>");
        sb.append("<sld:CssParameter name=\"stroke-linecap\">");
        sb.append("<ogc:Literal>butt</ogc:Literal>");
        sb.append("</sld:CssParameter>");
        sb.append("<sld:CssParameter name=\"stroke-linejoin\">");
        sb.append("<ogc:Literal>miter</ogc:Literal>");
        sb.append("</sld:CssParameter>");
        sb.append("<sld:CssParameter name=\"stroke-opacity\">");
        sb.append("<ogc:Literal>0.6</ogc:Literal>");
        sb.append("</sld:CssParameter>");
        sb.append("<sld:CssParameter name=\"stroke-width\">");
        sb.append("<ogc:Literal>0.5</ogc:Literal>");
        sb.append("</sld:CssParameter>");
        sb.append("<sld:CssParameter name=\"stroke-dashoffset\">");
        sb.append("<ogc:Literal>0</ogc:Literal>");
        sb.append("</sld:CssParameter>");
        sb.append("</sld:Stroke>");
        sb.append("</sld:PolygonSymbolizer>");

        return sb;
    }

    public StringBuffer addHeader(String layerName) {
        StringBuffer sb = new StringBuffer();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");

        sb.append("<sld:StyledLayerDescriptor version=\"1.0.0\" ");
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        sb.append("xsi:schemaLocation=\"http://www.opengis.net/sld");
        sb.append("/StyledLayerDescriptor.xsd\" ");
        sb.append("  xmlns=\"http://www.opengis.net/sld\"");
        sb.append("  xmlns:sld=\"http://www.opengis.net/sld\"");
        sb.append("  xmlns:ogc=\"http://www.opengis.net/ogc\"");
        sb.append("  xmlns:gml=\"http://www.opengis.net/gml\">");

        sb.append("<sld:NamedLayer >");
        sb.append("<sld:Name>");
        sb.append(layerName);
        sb.append("</sld:Name>");

        sb.append("<sld:UserStyle >");
        sb.append("<sld:Name>Customized_region_style</sld:Name>");
        sb.append("<sld:Title>geoserver style</sld:Title>");
        sb.append("<sld:Abstract>Generated by GeoServer</sld:Abstract>");
        sb.append("<sld:FeatureTypeStyle>");
        sb.append("<sld:Name>name</sld:Name>");
        sb.append("<sld:Title>title</sld:Title>");
        sb.append("<sld:Abstract>abstract</sld:Abstract>");
        sb.append("<sld:FeatureTypeName>Feature</sld:FeatureTypeName>");
        sb.append("<sld:SemanticTypeIdentifier>generic:geometry");
        sb.append("</sld:SemanticTypeIdentifier>");

        return (sb);
    }

    public StringBuffer addFooter(StringBuffer sbuff) {

        sbuff.append("</sld:FeatureTypeStyle>");
        sbuff.append("</sld:UserStyle>");
        sbuff.append("</sld:NamedLayer>");

        sbuff.append("</sld:StyledLayerDescriptor> ");

        return sbuff;
    }

	public int getPointSize() {
		return pointSize;
	}

	public void setPointSize(int pointSize) {
		this.pointSize = pointSize;
	}

}
