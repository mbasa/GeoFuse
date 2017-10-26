/**
 * 
 */
package geotheme.bean;

/**
 * @author mbasa
 *
 */
public class baseLayerBean {

	private int rank;
	private String url;
	private String attribution;
	private String subDomain;
	private String name;
	private boolean display = false;
	
	/**
	 * 
	 */
	public baseLayerBean() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}

	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the attribution
	 */
	public String getAttribution() {
		return attribution;
	}

	/**
	 * @param attribution the attribution to set
	 */
	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}

	/**
	 * @return the subDomain
	 */
	public String getSubDomain() {
		return subDomain;
	}

	/**
	 * @param subDomain the subDomain to set
	 */
	public void setSubDomain(String subDomain) {
		this.subDomain = subDomain;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the display
	 */
	public boolean isDisplay() {
		return display;
	}

	/**
	 * @param display the display to set
	 */
	public void setDisplay(boolean display) {
		this.display = display;
	}

}
