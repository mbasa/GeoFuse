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

import java.util.ArrayList;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class wfsRangeList {
    
        
    public static ArrayList<Double> createRangeList(String wfsHost, 
            String typeName,    String propertyName, 
            String cqlQuery,    String viewParams,
            String typeOfRange, int numRanges) {

        Logger LOGGER = LogManager.getLogger();
        
        ArrayList<Double> rangeList = new ArrayList<Double>();

        try {

            // *****************************************
            // * Getting values via wfs
            // *****************************************

            wfsParse wfs = new wfsParse(wfsHost);

            ArrayList<Double> arrList = wfs.parsePropertyVal( 
                    typeName,propertyName,cqlQuery,viewParams);
            
            Collections.sort(arrList);

            // *****************************************
            // * Creating the Ranges from Here
            // *****************************************
            if (typeOfRange.compareToIgnoreCase("EQRange") == 0) {
                double min = arrList.get(0).doubleValue();
                double max = arrList.get(arrList.size() - 1).doubleValue();
                double diff = (max - min) / numRanges;

                double d = min;
                for (int i = 0; i < numRanges; i++) {
                    rangeList.add(d);
                    d += diff;
                }
                rangeList.add(max);
                
            } else if (typeOfRange.compareToIgnoreCase("EQCount") == 0) {
                int count = (int) Math.ceil((float) arrList.size()
                        / (float) numRanges);

                for (int j = 0, k = 0; j < numRanges; j++, k += count) {
                    if (k > (arrList.size() - 1))
                        k = arrList.size() - 1;
                    rangeList.add(arrList.get(k));
                }
                rangeList.add(arrList.get(arrList.size() - 1));
                
            } else if(typeOfRange.compareToIgnoreCase("Geometric") == 0) {
                double min = Math.abs( arrList.get(0).doubleValue() );                
                double max = Math.abs( arrList.get(arrList.size() - 1).doubleValue() );
                
                if( min == 0 ) {
                    if( arrList.get(arrList.size() - 1).doubleValue() <= 1d )
                        min = 0.000001d;
                    else 
                        min = 1d;
                }
                
                double X = Math.pow( (max/min), (1.0d/(double)numRanges) );

                LOGGER.debug("min = {}, max = {}, X = {}, numRanges = {}",
                        min,max,X,numRanges);
                
                rangeList.add( arrList.get(0).doubleValue() );
                
                for(int j=1;j<numRanges;j++) {
                   rangeList.add( min * Math.pow(X, (double)j)); 
                }
                
                rangeList.add( max );
                
            } else if (typeOfRange.compareToIgnoreCase("Natural") == 0) {
            	int ii[] = getJenksBreaks(arrList,numRanges);
            	
            	rangeList.add(arrList.get(0));
            	
            	for(int l=0;l<ii.length;l++){
            		rangeList.add(arrList.get(ii[l]));
            	}            	
            } else if (typeOfRange.compareToIgnoreCase("Standard") == 0) {
                double mean = 0d;
                double meanSQ = 0d;
                double standard;

                for (int j = 0; j < arrList.size(); j++) {
                    mean   += arrList.get(j);
                    meanSQ += ( arrList.get(j) * arrList.get(j) );
                }

                mean = mean / arrList.size();
                standard = Math.sqrt(meanSQ / arrList.size() - mean * mean);

                rangeList.add(arrList.get(0));
                rangeList.add(mean);

                mean += standard;
                int counter = 0;
                
                while(mean < arrList.get(arrList.size() - 1) &&
                        counter < 8 ) {
                    rangeList.add(mean);
                    mean += standard;
                    counter ++;
                }
                
                rangeList.add(arrList.get(arrList.size() - 1));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rangeList;
    }

    public static int[] getJenksBreaks(ArrayList<Double> list, int
            numclass) {

        //int numclass;
        int numdata = list.size();

        if( numclass > list.size() ) {
            numclass = list.size();
        }

        
        double[][] mat1 = new double[numdata + 1][numclass
                                                  + 1];
        double[][] mat2 = new double[numdata + 1][numclass
                                                  + 1];
        //double[] st = new double[numdata];


        for (int i = 1; i <= numclass; i++) {
            mat1[1][i] = 1;
            mat2[1][i] = 0;
            for (int j = 2; j <= numdata; j++)
                mat2[j][i] = Double.MAX_VALUE;
        }
        double v = 0;
        for (int l = 2; l <= numdata; l++) {
            double s1 = 0;
            double s2 = 0;
            double w = 0;
            for (int m = 1; m <= l; m++) {
                int i3 = l - m + 1;

                double val = ((Double)list.get(i3
                        -1)).doubleValue();


                s2 += val * val;
                s1 += val;


                w++;
                v = s2 - (s1 * s1) / w;
                int i4 = i3 - 1;
                if (i4 != 0) {
                    for (int j = 2; j <= numclass; j++) {
                        if (mat2[l][j] >= (v + mat2[i4][j
                                                        - 1])) {
                            mat1[l][j] = i3;
                            mat2[l][j] = v + mat2[i4][j -
                                                      1];

                        };
                    };
                };
            };
            mat1[l][1] = 1;
            mat2[l][1] = v;
        };
        int k = numdata;


        int[] kclass = new int[numclass];


        kclass[numclass - 1] = list.size() - 1;


        for (int j = numclass; j >= 2; j--) {
            //System.out.println("rank = " + mat1[k][j]);
            int id =  (int) (mat1[k][j]) - 2;
            //System.out.println("val = " + list.get(id));
            //System.out.println(mat2[k][j]);


            kclass[j - 2] = id;


            k = (int) mat1[k][j] - 1;


        };
        return kclass;
    }

}
