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

package geotheme.bean;

public class metaDataBean {
	private String tabid      = null;
	private String linkLayer  = null;
	private String mapTable   = null;
	private String linkColumn = null;
	private String colNames[] = null;
	private String layerType  = null;
	
	public String getTabid() {
		return tabid;
	}
	public void setTabid(String tabid) {
		this.tabid = tabid;
	}
	public String getLinkLayer() {
		return linkLayer;
	}
	public void setLinkLayer(String linkLayer) {
		this.linkLayer = linkLayer;
	}
	public String getMapTable() {
		return mapTable;
	}
	public void setMapTable(String mapTable) {
		this.mapTable = mapTable;
	}
	public String getLinkColumn() {
		return linkColumn;
	}
	public void setLinkColumn(String linkColumn) {
		this.linkColumn = linkColumn;
	}
	public String[] getColNames() {
		return colNames;
	}
	public void setColNames(String[] colNames) {
		this.colNames = colNames;
	}
	public String getLayerType() {
		return layerType;
	}
	public void setLayerType(String layerType) {
		this.layerType = layerType;
	}
	
}
