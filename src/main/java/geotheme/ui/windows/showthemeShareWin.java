/**
 * パッケージ名：geotheme.ui
 * ファイル名  ：showthemeShareWin.java
 * 
 * @author mbasa
 * @since Aug 22, 2017
 */
package geotheme.ui.windows;

import java.util.ResourceBundle;

import com.vaadin.annotations.Theme;
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
public class showthemeShareWin extends Window {

	/**
	 * コンストラクタ
	 *
	 */
	public showthemeShareWin() {
	}

	/**
	 * コンストラクタ
	 *
	 * @param caption
	 */
	public showthemeShareWin(String caption) {
		super(caption);
	}

	/**
	 * コンストラクタ
	 *
	 * @param caption
	 * @param content
	 */
	public showthemeShareWin(String caption, Component content) {
		super(caption, content);
	}

	public void init( String urlStr, ResourceBundle props) {
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
		
		final TextField url = new TextField("URL: ");
		url.setWidth("100%");
		
		url.focus();
		url.setImmediate(true);
		url.setValue( urlStr );
		url.selectAll();
		
		final Button cancelButton = new Button(props.getString("SHR.CLOSE_BTN"));
		
		cancelButton.addClickListener(new Button.ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				closeWin();				
			}
		});
		
		HorizontalLayout buttonLayout = 
				new HorizontalLayout(cancelButton);
		buttonLayout.setMargin(true);
		buttonLayout.setSpacing(true);
		
		formLayout.addComponents( url );

		winLayout.addComponents(formLayout,buttonLayout);
		winLayout.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);
		
		this.setContent(winLayout);
	}
	
	private void closeWin() {
		this.close();
	}
}
