/**
 * 
 */
package geotheme.ui;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import geotheme.ui.views.geofuseMainView;

/**
 * 
 * 説明：
 *
 */

@SuppressWarnings("serial")
@Theme("geofuse")
public class geofuseUI extends UI {

	@WebServlet(value = {"/*","/VAADIN/*"}, asyncSupported = true)
	@VaadinServletConfiguration(
			productionMode = false, 
			ui             = geofuseUI.class,
			widgetset      = "geotheme.ui.widgetset.GeofuseWidgetset" )
	public static class Servlet extends VaadinServlet {
	}

	/**
	 * 
	 * コンストラクタ
	 *
	 */
	public geofuseUI() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * コンストラクタ
	 *
	 * @param content
	 */
	public geofuseUI(Component content) {
		super(content);
		// TODO Auto-generated constructor stub
	}

	
	/* (non-Javadoc)
	 * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
	 */
	@Override
	protected void init( VaadinRequest request ) {
	    this.setContent(new geofuseMainView() );
	}
}
