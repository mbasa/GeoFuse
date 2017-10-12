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

package geotheme.csv;

import java.util.*;
import geotheme.csv.parseCSV;
import geotheme.db.*;

/**
 * Processes the CSV data
 */
public class processCSV {


    private String db_metadata  = new String();
    private String db_dynlinker = new String();
    private String db_csvdata_schema = new String();

    public processCSV() {

        ResourceBundle rdb = ResourceBundle.getBundle(
                "properties.database");

        this.db_metadata       = rdb.getString("DB.METADATA.TABLE");
        this.db_dynlinker      = rdb.getString("DB.DYNAMIC_LINKER.TABLE");
        this.db_csvdata_schema = rdb.getString("DB.CSVDATA_SCHEMA");

    }

    public String process(String inputString, String layerName ) {
        boolean isLatLon = false;

        //inputString = (URLDecoder.decode(inputString,"UTF-8"));

        String pStr[][] = parseCSV.parse(inputString);

        if(pStr == null ) {
            return ("Error: problem with input data");
        }

        mapLinkerCtl dlc = new mapLinkerCtl( this.db_dynlinker );
        Map<String,String[]> dynamicMap = dlc.getMapLinkData();

        if( !dynamicMap.containsKey(pStr[0][0]) ) {
            int pStrLen = pStr[0].length;

            if( pStr[0][pStrLen-1].isEmpty() )
                pStrLen--;

            String cLon = pStr[0][pStrLen-2];
            String cLat = pStr[0][pStrLen-1];

            if(cLon.equalsIgnoreCase("lon") && 
                    cLat.equalsIgnoreCase("lat") ) {

                isLatLon = true;

            }
            else {
                return ("Error: 1st column does not match any Mapped column");
            }
        }

        metaDataCtl mdc = new metaDataCtl( this.db_metadata );

        Random rand = new Random();

        String tableName = this.db_csvdata_schema + 
                ".mb_"+(new Date()).getTime()+ "_" + 
                rand.nextInt(10000);

        if( isLatLon ) {
            String rs = mdc.processDataLatLon(pStr, tableName); 
            if( rs != null ) {
                return("Error: Data entry "+rs);
            }
        }
        else {
            String rs = mdc.processData(pStr, tableName);
            if( rs != null ) {
                return("Error: Data entry "+rs);
            }
        }

        if( !mdc.setMetaInfo(pStr[0], dynamicMap, this.db_metadata, 
                tableName,isLatLon,layerName) ) {
            return("Error: Catalog entry");
        }


        return( tableName );
    }

}
