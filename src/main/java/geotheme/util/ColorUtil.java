/**
 * 
 */
package geotheme.util;

/**
 * @author mbasa
 *
 */
public class ColorUtil {

	/**
	 * 
	 */
	public ColorUtil() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param colorStr in form "#FFFFFF"
	 * @return rgb int array
	 */
	public static int[] CssHexToRGB( String colorStr ) {
		int rgb[] = {0,0,0};
		
		rgb[0] = Integer.valueOf( colorStr.substring( 1, 3 ), 16 );
		rgb[1] = Integer.valueOf( colorStr.substring( 3, 5 ), 16 );
		rgb[2] = Integer.valueOf( colorStr.substring( 5, 7 ), 16 );
		
		return rgb;
	}
}
