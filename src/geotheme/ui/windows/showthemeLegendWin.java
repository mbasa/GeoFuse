/**
 * 
 */
package geotheme.ui.windows;

import geotheme.util.ColorUtil;

import java.util.ArrayList;
import java.util.ResourceBundle;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Item;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * @author mbasa
 * @since 2015/06/17
 */

@SuppressWarnings("serial")
@Theme("geofuse")
public class showthemeLegendWin extends Window {

	private final String RANGE_ID = "range";
	private final String COLOR_ID = "color";
	
	private Table table;
	private Button button1;
	private Button button2;
	
	/**
	 *　
	 */
	public showthemeLegendWin() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param caption
	 */
	public showthemeLegendWin(String caption) {
		super(caption);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param caption
	 * @param content
	 */
	public showthemeLegendWin(String caption, Component content) {
		super(caption, content);
		// TODO Auto-generated constructor stub
	}

	public void init( ArrayList<String> colorVals, 
			ArrayList<Double> rangeVals, ResourceBundle showthemeProps ) {
		this.setModal(true);
		this.setResizable(false);
		this.setClosable(false);
		
		this.setHeight("300px");
		this.setWidth("400px");
		
		VerticalLayout winLayout = new VerticalLayout();
		
		table = new Table();
		table.setColumnReorderingAllowed(false);
		table.setSortEnabled(false);
		table.setEditable(true);
		table.setSizeFull();
		
		table.addContainerProperty(COLOR_ID, ColorPicker.class, null);
		table.addContainerProperty(RANGE_ID, Double.class, 0.0);
		
		table.setColumnHeader(COLOR_ID, 
				showthemeProps.getString("LEGEND_WIN.COLOR_COLUMN")/*"色"*/);
		table.setColumnHeader(RANGE_ID, 
				showthemeProps.getString("LEGEND_WIN.RANGE_COLUMN")/*"範囲"*/);
		
		table.setColumnAlignment(COLOR_ID, Align.CENTER);
		table.setColumnAlignment(RANGE_ID, Align.CENTER);
		
		for( int i=0; i< rangeVals.size(); i++ ) {
			
			int mRgb[]     = ColorUtil.CssHexToRGB( colorVals.get(i) );
			ColorPicker cp = new ColorPicker("item:"+i,
					new Color(mRgb[0],mRgb[1],mRgb[2]) );
			
			table.addItem(new Object[] {
					cp,
					rangeVals.get(i)
			},"item"+i);
			
			if( i == rangeVals.size()-1 ) {
				cp.setEnabled(false);
				cp.setVisible(false);
			}
		}
		

		button1 = new Button(
				showthemeProps.getString("LEGEND_WIN.SAVE_BTN"));
		button2 = new Button(
				showthemeProps.getString("LEGEND_WIN.CANCEL_BTN"));
		
		button2.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				closeWin();
			}
		});
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		buttons.setMargin(true);
				
		buttons.addComponent(button1);
		buttons.addComponent(button2);
		
		winLayout.addComponent(table);
		winLayout.addComponent(buttons);
		
		winLayout.setComponentAlignment(buttons, Alignment.MIDDLE_CENTER);
		
		winLayout.setSizeFull();
		winLayout.setExpandRatio(table, 1);
		
		this.setContent(winLayout);		
	}

	public void closeWin() {
		
		for( Object id : table.getItemIds() ) {
			
			Item item  = table.getItem( id );
				
			ColorPicker cp = (ColorPicker) item.getItemProperty(COLOR_ID).getValue();
			cp.hidePopup();			
		}
			
		this.close();
	}
	
	/**
	 * @return the table
	 */
	public Table getTable() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	/**
	 * @return the button1
	 */
	public Button getButton1() {
		return button1;
	}

	/**
	 * @param button1 the button1 to set
	 */
	public void setButton1(Button button1) {
		this.button1 = button1;
	}

	/**
	 * @return the button2
	 */
	public Button getButton2() {
		return button2;
	}

	/**
	 * @param button2 the button2 to set
	 */
	public void setButton2(Button button2) {
		this.button2 = button2;
	}

	/**
	 * @return the rANGE_ID
	 */
	public String getRANGE_ID() {
		return RANGE_ID;
	}

	/**
	 * @return the cOLOR_ID
	 */
	public String getCOLOR_ID() {
		return COLOR_ID;
	}

}
