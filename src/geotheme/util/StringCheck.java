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

import java.util.regex.*;

public class StringCheck {

	public static boolean isNumeric(String number){
		boolean isValid = false;

		/*Number: A numeric value will have following format:
		    ^[-+]?: Starts with an optional "+" or "-" sign.
			[0-9]*: May have one or more digits.
			\\.? : May contain an optional "." (decimal point) character.
			[0-9]+$ : ends with numeric digit.
		 */

		//Initialize reg ex for numeric data.
		String expression = "^[-+]?[0-9]*\\.?[0-9]+$";
		CharSequence inputStr = number;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		
		if(matcher.matches()){
			isValid = true;
		}
		
		return isValid;
	}


}
