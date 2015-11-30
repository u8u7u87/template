package slimer.zk.model;

/**
 * Created by jjh on 15/10/26.
 */
public class ZKConfig {

    private String zkURL;

    private int zkTimeOut;

    private String path;

    public ZKConfig(){

    }

    public ZKConfig(String zkURL, int zkTimeOut){
        this.zkURL = zkURL;
        this.zkTimeOut = zkTimeOut;
    }

    public String getZkURL() {
        return zkURL;
    }

    public void setZkURL(String zkURL) {
        this.zkURL = zkURL;
    }

    public int getZkTimeOut() {
        return zkTimeOut;
    }

    public void setZkTimeOut(int zkTimeOut) {
        this.zkTimeOut = zkTimeOut;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
