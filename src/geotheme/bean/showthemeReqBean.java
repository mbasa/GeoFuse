/**
 * パッケージ名：geotheme.bean
 * ファイル名  ：showthemeReqBean.java
 * 
 * @author mbasa
 * @since Aug 22, 2017
 */
package geotheme.bean;

/**
 * 説明：
 *
 */
public class showthemeReqBean {

    private String layer     = null;
    private String propName  = null;
    private String typeRange = null;
    private String numRange  = null;
    private String cqlString = null;
    private String color     = null;
    private String bnd       = null;
    private String baselayer = null;
    private String fromdate  = null;
    private String todate    = null;
    
    /**
     * コンストラクタ
     *
     */
    public showthemeReqBean() {
    }

    /**
     * @return layer を取得する
     */
    public String getLayer() {
        return layer;
    }

    /**
     * @param layer layer を設定する
     */
    public void setLayer(String layer) {
        if( layer != null && !layer.isEmpty() )
            this.layer = layer;
    }

    /**
     * @return colors を取得する
     */
    public String getColor() {
        return color;
    }

    /**
     * @param colors colors を設定する
     */
    public void setColor(String color) {
        if( color != null && !color.isEmpty() )
            this.color = color;
    }

    /**
     * @return bnd を取得する
     */
    public String getBnd() {
        return bnd;
    }

    /**
     * @param bnd bnd を設定する
     */
    public void setBnd(String bnd) {
        if( bnd != null && !bnd.isEmpty() )
        this.bnd = bnd;
    }

    /**
     * @return baselayer を取得する
     */
    public String getBaselayer() {
        return baselayer;
    }

    /**
     * @param baselayer baselayer を設定する
     */
    public void setBaselayer(String baselayer) {
        if( baselayer != null && !baselayer.isEmpty() )
            this.baselayer = baselayer;
    }

    /**
     * @return propName を取得する
     */
    public String getPropName() {
        return propName;
    }

    /**
     * @param propName propName を設定する
     */
    public void setPropName(String propName) {
        if( propName != null && !propName.isEmpty() )
            this.propName = propName;
    }

    /**
     * @return typeRange を取得する
     */
    public String getTypeRange() {
        return typeRange;
    }

    /**
     * @param typeRange typeRange を設定する
     */
    public void setTypeRange(String typeRange) {
        if( typeRange != null && !typeRange.isEmpty() )
            this.typeRange = typeRange;
    }

    /**
     * @return numRange を取得する
     */
    public String getNumRange() {
        return numRange;
    }

    /**
     * @param numRange numRange を設定する
     */
    public void setNumRange(String numRange) {
        if( numRange != null && !numRange.isEmpty() )
            this.numRange = numRange;
    }

    /**
     * @return cqlString を取得する
     */
    public String getCqlString() {
        return cqlString;
    }

    /**
     * @param cqlString cqlString を設定する
     */
    public void setCqlString(String cqlString) {
        if( cqlString != null && !cqlString.isEmpty() )
            this.cqlString = cqlString;
    }

    /**
     * @return fromdate を取得する
     */
    public String getFromdate() {
        return fromdate;
    }

    /**
     * @param fromdate fromdate を設定する
     */
    public void setFromdate(String fromdate) {
        if(fromdate != null && !fromdate.isEmpty())
            this.fromdate = fromdate;
    }

    /**
     * @return todate を取得する
     */
    public String getTodate() {
        return todate;
    }

    /**
     * @param todate todate を設定する
     */
    public void setTodate(String todate) {
        if( todate != null && !todate.isEmpty() )
            this.todate = todate;
    }

}
