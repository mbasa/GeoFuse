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

package geotheme.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.ResourceBundle;

public class connectionCtl {

	private static String hostname = null;
	private static String username = null;
	private static String password = null;
	private static String dbname   = null;

	public connectionCtl() {
	}

	public static Connection getConnection() throws Exception{

		if(hostname == null && username == null && 
				password == null && dbname == null ) {

			ResourceBundle rdb = 
					ResourceBundle.getBundle("properties.database");

			dbname    = rdb.getString("DB.NAME");
			hostname  = rdb.getString("DB.HOST");
			username  = rdb.getString("DB.USER");
			password  = rdb.getString("DB.PASSWORD");

		}
		
		Class.forName("org.postgresql.Driver");

		String url = "jdbc:postgresql://"+hostname+"/"+dbname;

		Properties prop = new Properties();
		prop.setProperty("user"    , username);
		prop.setProperty("password", password);

		Connection con = DriverManager.getConnection(url,prop);

		return con;

	}
}
