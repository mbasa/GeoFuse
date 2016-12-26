/**
 * パッケージ名：geotheme.bean
 * ファイル名  ：overlayLayerBean.java
 * 
 * @author mbasa
 * @since Dec 26, 2016
 */
package geotheme.bean;

/**
 * 説明：
 *
 */
public class overlayLayerBean {

    private int     rank;
    private String  url;
    private String  layers;
    private String  name;
    private int     minZoom;
    private boolean active  = false;
    private boolean display = false;

    /**
     * コンストラクタ
     *
     */
    public overlayLayerBean() {
    }

    /**
     * @return rank を取得する
     */
    public int getRank() {
        return rank;
    }

    /**
     * @param rank rank を設定する
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * @return url を取得する
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url url を設定する
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return layers を取得する
     */
    public String getLayers() {
        return layers;
    }

    /**
     * @param layers layers を設定する
     */
    public void setLayers(String layers) {
        this.layers = layers;
    }

    /**
     * @return name を取得する
     */
    public String getName() {
        return name;
    }

    /**
     * @param name name を設定する
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return active を取得する
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active active を設定する
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return display を取得する
     */
    public boolean isDisplay() {
        return display;
    }

    /**
     * @param display display を設定する
     */
    public void setDisplay(boolean display) {
        this.display = display;
    }

    /**
     * @return minZoom を取得する
     */
    public int getMinZoom() {
        return minZoom;
    }

    /**
     * @param minZoom minZoom を設定する
     */
    public void setMinZoom(int minZoom) {
        this.minZoom = minZoom;
    }


}
