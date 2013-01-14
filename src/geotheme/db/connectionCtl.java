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

public class connectionCtl {

    private String hostname = null;
    private String username = null;
    private String password = null;
    private String dbname   = null;
    
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public connectionCtl(String host,String user,
            String pass,String db) {
        this.hostname = host;
        this.username = user;
        this.password = pass;
        this.dbname   = db;
    }
    
    public Connection getConnection() throws Exception{
        Class.forName("org.postgresql.Driver");

        String url = "jdbc:postgresql://"+this.hostname+"/"+this.dbname;

        Properties prop = new Properties();
        prop.setProperty("user", this.username);
        prop.setProperty("password", this.password);

        Connection con = DriverManager.getConnection(url,prop);
        
        return con;

    }
}
