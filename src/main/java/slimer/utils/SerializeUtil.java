package slimer.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Object Serialize Util.
 * @author chuaiqing.
 *
 */
public class SerializeUtil {
    /**
     * Serialize
     * @param object
     * @return
     */
    public static byte[] serialize(Object object) {
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
            //
        }
        return null;
    }

    /**
     * Unserialize
     * @param bytes
     * @return
     */
    public static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            //
        }
        return null;
    }
}
