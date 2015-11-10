/**
 * パッケージ名：geotheme.ui
 * ファイル名  ：showthemePdfWin.java
 * 
 * @author mbasa
 * @since Jun 30, 2015
 */
package geotheme.ui;

import geotheme.bean.themeBean;

import java.util.Date;
import java.util.ResourceBundle;

import org.vaadin.addon.leaflet.shared.Bounds;

import com.vaadin.annotations.Theme;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * 説明：
 *
 */
@SuppressWarnings("serial")
@Theme("geofuse")
public class showthemePdfWin extends Window {

	/**
	 * コンストラクタ
	 *
	 */
	public showthemePdfWin() {
	}

	/**
	 * コンストラクタ
	 *
	 * @param caption
	 */
	public showthemePdfWin(String caption) {
		super(caption);
	}

	/**
	 * コンストラクタ
	 *
	 * @param caption
	 * @param content
	 */
	public showthemePdfWin(String caption, Component content) {
		super(caption, content);
	}

	private String titleVal = new String();
	private String descVal  = new String();
	
	public void init( themeBean tb, Bounds bounds, ResourceBundle props) {
		this.setModal(true);
		this.setResizable(false);
		this.setClosable(false);
		
		this.setHeight("200px");
		this.setWidth("450px");
		
		VerticalLayout winLayout = new VerticalLayout();
		
		FormLayout formLayout = new FormLayout();
		formLayout.setSpacing(true);
		formLayout.setMargin(true);
		formLayout.setSizeFull();
		
		final TextField title = new TextField(props.getString("PDF.TITLE")+":");
		title.setWidth("100%");
		
		title.focus();
		title.setImmediate(true);
		
		final TextField desc  = new TextField(props.getString("PDF.DESC")+":");	
		desc.setWidth("100%");

		final Button printButton  = new Button(props.getString("PDF.CREATE_BTN"));
		final Button cancelButton = new Button(props.getString("PDF.CANCEL_BTN"));
		
		final BrowserWindowOpener bwo = new BrowserWindowOpener("");
		
		bwo.setWindowName("_pdf");		
		bwo.extend(printButton);
		
		final StringBuffer sb = new StringBuffer();
		sb.append("/geofuse/wms?REQUEST=GetPDFGraphic&FORMAT=image/png");
		sb.append("&TRANSPARENT=TRUE&SRS=EPSG:4326&SERVICE=wms");
		sb.append("&LAYERS=").append(tb.getLayerName());
		
		sb.append("&BBOX=");
		sb.append(bounds.getSouthWestLon()).append(",");
		sb.append(bounds.getSouthWestLat()).append(",");
		sb.append(bounds.getNorthEastLon()).append(",");
		sb.append(bounds.getNorthEastLat());
		
		sb.append("&VIEWPARAMS=").append(tb.getViewParams());
		sb.append("&CQL_FILTER=").append(tb.getCql());
		
		sb.append("&DATE=").append(new Date().getTime());

		printButton.setEnabled(false);
		printButton.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {	
				closeWin();
			}
		});
		
		cancelButton.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				closeWin();				
			}
		});
		
		title.addTextChangeListener(new TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				titleVal = event.getText();
				bwo.setUrl(sb.toString()+"&PDF_TITLE="+titleVal+
						"&PDF_NOTE="+descVal);
				
				if(titleVal.length() > 0 && descVal.length() > 0 ) {
					printButton.setEnabled(true);
				}
				else {
					printButton.setEnabled(false);
				}
			}
		});
		
		desc.addTextChangeListener(new TextChangeListener() {
			
			@Override
			public void textChange(TextChangeEvent event) {
				descVal = event.getText();
				bwo.setUrl(sb.toString()+"&PDF_TITLE="+titleVal+
						"&PDF_NOTE="+descVal);
				
				if(titleVal.length() > 0 && descVal.length() > 0 ) {
					printButton.setEnabled(true);
				}
				else {
					printButton.setEnabled(false);
				}

			}
		});
		
		HorizontalLayout buttonLayout = 
				new HorizontalLayout(printButton,cancelButton);
		buttonLayout.setMargin(true);
		buttonLayout.setSpacing(true);
		
		formLayout.addComponents(title,desc);

		winLayout.addComponents(formLayout,buttonLayout);
		winLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
		
		this.setContent(winLayout);
	}
	
	private void closeWin() {
		this.close();
	}
}
