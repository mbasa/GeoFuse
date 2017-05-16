/**
 * 
 */
package geotheme.ui;

import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.servlet.annotation.WebServlet;

import org.vaadin.sliderpanel.SliderPanel;
import org.vaadin.sliderpanel.SliderPanelStyles;
import org.vaadin.sliderpanel.client.SliderMode;
import org.vaadin.sliderpanel.client.SliderTabPosition;

import geotheme.db.connectionPoolHolder;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import geotheme.csv.*;
import geotheme.util.UrlUtil;

/**
 * 
 * 説明：
 *
 */

@SuppressWarnings("serial")
@Theme("geofuse")
public class geofuseUI extends UI {

	final Table table = new Table();
	private ResourceBundle resourceBundle;
	
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

		resourceBundle = ResourceBundle.getBundle(
				"properties/geofuseUI",request.getLocale() );
		
		final HorizontalLayout mainLayout = new HorizontalLayout();
		//mainLayout.setSizeFull();
		mainLayout.setWidth("100%");
		setContent(mainLayout);

		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);

		ThemeResource img1Res = new ThemeResource("graphics/shapeimage_1.png");
		ThemeResource img2Res = new ThemeResource("graphics/shapeimage_2.png");

		final Image img1 = new Image(null,img1Res);
		final Image img2 = new Image(null,img2Res);

		final HorizontalLayout imgLayout = new HorizontalLayout();
		//imgLayout.setMargin(true);
		imgLayout.setSpacing(true);
		imgLayout.addComponents( img1,img2 );

		final Label geofuseLab = new Label();
		geofuseLab.setContentMode(ContentMode.HTML);
		geofuseLab.setValue("<h1>"+
				resourceBundle.getString("MAIN.TITLE")+
				"</h1><p><h3>"+
				resourceBundle.getString("MAIN.DESC")+
				"<h3></p>");

		Label descLab = new Label();
		//descLab.setHeight("250px");
		descLab.setContentMode(ContentMode.HTML);
		descLab.setWidth("700px");
		descLab.setValue( resourceBundle.getString("MAIN.EXPLAIN") );

		final TextArea csvBox = new TextArea(
                resourceBundle.getString("MSG.CSVBOX_TITLE") );
        csvBox.setWidth("700px");
        csvBox.setRequired(true);

        final TextArea layerNameBox = new TextArea(
                resourceBundle.getString("MSG.LAYERNAME_TITLE") );
        layerNameBox.setWidth("350px");
        layerNameBox.setHeight("25px");
        layerNameBox.setRequired(true);

		final TextArea urlBox = new TextArea();
		urlBox.setWidth("700px");
		urlBox.setHeight("35px");

		final BrowserWindowOpener bOpener = new BrowserWindowOpener("");
		bOpener.setWindowName("_geotab");

		final Button urlDispButton = new Button(
				resourceBundle.getString("BTN.DISPLAY_MAP") );
		urlDispButton.setEnabled(false);

		bOpener.extend(urlDispButton);

		final Button csvSubmitButton = new Button(
				resourceBundle.getString("BTN.SUBMIT") );
		
		csvSubmitButton.addClickListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {

				if( csvBox.getValue().length() < 10 ) {
					Notification.show(
							resourceBundle.getString("ERR.NOT_ENOUGH_DATA") );
					return;
				}

				processCSV pcsv = new processCSV();
				String result   = pcsv.process( 
				        csvBox.getValue(),layerNameBox.getValue() );

				if( result.startsWith("Error") ) {
					Notification.show( result );
				}
				else {
					StringBuffer sb = new StringBuffer();

					sb.append( UrlUtil.getUrl(
							(VaadinServletRequest)VaadinService.getCurrentRequest()) );
					sb.append("/ui/showtheme?layer=");
					sb.append(result);

					urlBox.setValue( sb.toString() );
					urlDispButton.setEnabled(true);
					bOpener.setUrl( sb.toString() );
					
					setDbTable();
				}
				csvSubmitButton.setEnabled(false);
			}
		});

		final Button csvResetButton = new Button(
				resourceBundle.getString("BTN.RESET") );
		
		csvResetButton.addClickListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				csvBox.clear();
				csvBox.setValue("");
				layerNameBox.clear();
				layerNameBox.setValue("");
				urlBox.clear();
				urlBox.setValue("");
				bOpener.setUrl("");

				urlDispButton.setEnabled(false);
				csvSubmitButton.setEnabled(true);
			}
		});

		final HorizontalLayout csvLayout = new HorizontalLayout();
		csvLayout.setSpacing(true);
		csvLayout.addComponents(csvSubmitButton,csvResetButton);

		layout.addComponents(geofuseLab,imgLayout,descLab,
				csvBox,layerNameBox,csvLayout,urlBox,urlDispButton);		
		
		/**
		 * SliderPanel Portion
		 */
		 VerticalLayout gridLayout = this.setUpGrid();
		
		 SliderPanel sliderPanel = new SliderPanel(
				gridLayout,true,SliderMode.RIGHT);
		
		sliderPanel.setStyleName(SliderPanelStyles.COLOR_BLUE);
		sliderPanel.setCaption(
				resourceBundle.getString("SLDR.PREVIOUS_CAPTION") );
		
		sliderPanel.setTabPosition(SliderTabPosition.MIDDLE);
		sliderPanel.setHeight("50%");
		
		Label helpLab = new Label();
		helpLab.setValue(
				resourceBundle.getString("SLDR.HELP_MESSAGE") );
		helpLab.setContentMode(ContentMode.HTML);
		helpLab.setWidth("275px");
		
		SliderPanel helpSliderPanel = new SliderPanel(
				helpLab,false,SliderMode.LEFT);
		helpSliderPanel.setCaption(
				resourceBundle.getString("SLDR.HELP_CAPTION") );
		
		helpSliderPanel.setStyleName(SliderPanelStyles.COLOR_GREEN);
		helpSliderPanel.setTabPosition(SliderTabPosition.MIDDLE);
		helpSliderPanel.setHeight("95%");
		
		mainLayout.addComponent(helpSliderPanel);
		mainLayout.addComponent(layout);
		mainLayout.addComponent(sliderPanel);
		mainLayout.setExpandRatio(layout, 1);

		sliderPanel.scheduleCollapse(300);
	}

	/**
	 * 
	 * 
	 * 
	 * @return
	 */
	private VerticalLayout setUpGrid() {
		
		VerticalLayout gridLayout = new VerticalLayout();
		
		final Button showMapBtn      = new Button();
		
		gridLayout.setMargin(true);
		gridLayout.setSpacing(true);
		gridLayout.addComponents(table,showMapBtn);
		gridLayout.setExpandRatio(table, 1);
		
		table.setHeight("200px");
		table.setWidth("650px");
		table.setStyleName("valo");
		
		showMapBtn.setCaption(
				resourceBundle.getString("GRID.BTN_SHOW_MAP") );
		showMapBtn.setEnabled(false);

		final BrowserWindowOpener bOpener = new BrowserWindowOpener("");
		bOpener.setWindowName("_geotab");
		bOpener.extend( showMapBtn );

		table.addItemClickListener(new ItemClickListener() {

			@Override
			public void itemClick(ItemClickEvent event) {

				String rowId = event.getItemId().toString();

				if( rowId != null ) {
					String url = UrlUtil.getUrl(
							(VaadinServletRequest)VaadinService.getCurrentRequest())
							+"/ui/showtheme?layer="
							+rowId;

					bOpener.setUrl(url);					
					showMapBtn.setEnabled(true);
				}
			}
		});

		setDbTable();
		
		return gridLayout;
	}
	
	/**
	 * 
	 * 
	 *
	 */
	private void setDbTable() {
	    table.setContainerDataSource( setContainer() );
	    table.setCaption(resourceBundle.getString("GRID.MAIN_CAPTION"));

	    table.setColumnHeader("layername", 
	            resourceBundle.getString("GRID.COL_LAYERNAME") );
	    table.setColumnHeader("ddate", 
	            resourceBundle.getString("GRID.COL_DDATE") );
	    table.setColumnHeader("colnames",
	            resourceBundle.getString("GRID.COL_COLNAMES") );
	    table.setColumnHeader("linkcolumn",
	            resourceBundle.getString("GRID.COL_LINKCOLUMN") );
	    table.setColumnHeader("layertype",
	            resourceBundle.getString("GRID.COL_LAYERTYPE") );

	    table.setSelectable(true);
	    table.setNullSelectionAllowed(false);
	    table.setVisibleColumns("layername","ddate","layertype",
	            "linkcolumn","colnames");		
	}
	/**
	 * 
	 * 
	 * 
	 * @return
	 */
	private SQLContainer setContainer() {

		SQLContainer container = null;
		
		FreeformQuery query = new FreeformQuery(
				"select * from geofuse.metadata order by ddate desc",
				connectionPoolHolder.getConnectionPool(),
				"tabid");

		try {
			container = new SQLContainer( query );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return container;
	}

}
