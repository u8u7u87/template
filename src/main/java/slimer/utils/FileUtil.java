package slimer.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by jjh on 15/10/26.
 */
public class FileUtil {

    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static String getLocalFileStrByClasspath(String fileName) throws URISyntaxException, IOException {

        String filePath = getLocalPath(fileName);

        return readFilePath(filePath);

    }

    /**
     * 获取本地path的地址
     * @return
     */
    public static String getLocalPath(String path) throws URISyntaxException {

        URL url = Thread.currentThread().getContextClassLoader().getResource("");

        if (null == url) {
            throw new RuntimeException(path + "is not exist");
        }

        File file = new File(url.toURI());
        return file.getAbsolutePath() + path;

    }

    /**
     * 往file路径写入data内容
     * @param path
     * @param data
     */
    public static void writeFilePath(String path, String data) throws IOException {

        BufferedWriter bw = null;

        try {
            // 先检查路径是否已经有文档或者存在
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }

            FileWriter fw = new FileWriter(file);

            bw = new BufferedWriter(fw);

            bw.write(data);
        } finally {
            if (null != bw) {
                bw.close();
            }
        }

    }

    public static String readFilePath(String path) throws IOException {
        StringBuffer fileContext = new StringBuffer();

        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(path));

            String tempStr = "";

            // 循环读取文件，每次读取1024个字节
            while ((tempStr = br.readLine()) != null) {
                fileContext.append(tempStr);
            }

            return fileContext.toString();
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error(path + " close error");
                }
            }
        }

    }

}
