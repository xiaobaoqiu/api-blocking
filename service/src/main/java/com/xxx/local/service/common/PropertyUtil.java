package com.xxx.local.service.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

/**
 * 用于获取属性文件内容的工具类
 *
 * @author xiaobaoqiu  Date: 17-1-25 Time: 上午11:47
 */
public class PropertyUtil {

    /**
     * 日志类。
     */
    private static Log log = LogFactory.getLog(PropertyUtil.class);

    /**
     * 默认属性文件名
     */
    public static final String DEFAULT_PROPERTY_FILE = "ApplicationResources.properties";

    /**
     * 附加文件的前缀
     */
    private static final String ADD_PROPERTY_PREFIX = "add.property.file.";

    /**
     * 属性文件扩展名
     */
    private static final String PROPERTY_EXTENSION = ".properties";

    /**
     * 属性键值的保存对象
     */
    private static TreeMap<String, String> props = new TreeMap<>();

    /**
     * 已读入的属性文件名列表
     */
    private static Set<String> files = new HashSet<>();

    /**
     * 类加载时，进行文件属性读取的初始化。
     */
    static {
        StringBuilder key = new StringBuilder();
        load(DEFAULT_PROPERTY_FILE);
        if (props != null) {
            for (int i = 1; ; i++) {
                key.setLength(0);
                key.append(ADD_PROPERTY_PREFIX);
                key.append(i);
                String path = getProperty(key.toString());
                if (path == null) {
                    break;
                }
                addPropertyFile(path);
            }
        }
        overrideProperties();
    }

    /**
     * 读取指定的属性文件。
     *
     * <p>
     * 读取并追加指定属性文件的内容。
     * </p>
     *
     * @param name 属性文件名
     */
    private static void load(String name) {
        StringBuilder key = new StringBuilder();
        Properties p = readPropertyFile(name);
        for (Map.Entry<Object, Object> e : p.entrySet()) {
            // 追加读入的所有内容
            props.put((String) e.getKey(), (String) e.getValue());
        }

        if (p != null) {
            for (int i = 1; ; i++) {
                key.setLength(0);
                key.append(ADD_PROPERTY_PREFIX);
                key.append(i);
                String addfile = p.getProperty(key.toString());
                if (addfile == null) {
                    break;
                }
                String path = getPropertiesPath(name, addfile);
                addPropertyFile(path);
            }
        }
    }

    /**
     * 读取指定的属性文件。
     *
     * <p>
     * 读取并追加指定属性文件的内容。
     * </p>
     * @param name 属性文件名
     * @return 属性列表
     */
    private static Properties readPropertyFile(String name) {
        // 获取当前容器的类加载器，并读取WEB-INF/classes下的属性文件。
        // 或使用主线程的类加载器，通过JNLP方式获取资源内容。
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
        if (is == null) {
            is = PropertyUtil.class.getResourceAsStream("/" + name);
        }
        InputStreamReader reader = null;
        if (is != null) {
            reader = new InputStreamReader(is);
        }

        Properties p = new Properties();
        try {
            try {
                p.load(reader);
                files.add(name);

            } catch (NullPointerException e) {
                System.err.println("!!! PANIC: Cannot load " + name + " !!!, ");
                System.err.println(ExceptionUtil.getStackTrace(e));
            } catch (IOException e) {
                System.err.println("!!! PANIC: Cannot load " + name + " !!!");
                System.err.println(ExceptionUtil.getStackTrace(e));
            }
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                log.error("", e);
            }
        }
        return p;
    }

    /**
     * 通过命令行选项&quot;-D&quot; 覆盖从属性文件中读取的系统属性内容。
     */
    private static void overrideProperties() {
        Enumeration<String> enumeration = Collections.enumeration(props.keySet());
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = System.getProperty(name);
            if (value != null) {
                props.put(name, value);
            }
        }
    }

    /**
     * 读取指定的追加属性文件的内容。
     *
     * <p>
     *  每个文件内容只读取一次，重复执行该方法无效。
     *  属性文件扩展名".properties"可省略。
     * </p>
     *
     * @param name 属性文件名
     */
    public static void addPropertyFile(String name) {
        if (!name.endsWith(PROPERTY_EXTENSION)) {
            StringBuilder nameBuf = new StringBuilder();
            nameBuf.append(name);
            nameBuf.append(PROPERTY_EXTENSION);
            name = nameBuf.toString();
        }
        if (!files.contains(name)) {
            load(name);
        }
    }

    /**
     * 获取指定key的属性值。
     *
     * <p>
     *  当参数由&quot;@&quot;开头时，属性值被看作一个间接引用，并做为一个新属性名重新搜索。
     *  为避免<code>key=@key</code>情况下的死循环，直接将<code>@key</code>结果返回。
     *  当需要设置以&quot;@&quot;开头的属性值时，请使用&quot;@@&quot;方式避免间接属性名引用。
     * </p>
     *
     * @param key 属性名
     * @return 指定属性名的属性值
     */
    public static String getProperty(String key) {
        String result = props.get(key);

        // (key)=@(key)时，避免死循环
        if (result != null && result.equals("@" + key)) {
            return result;
        }
        // 属性值以@@开头时，将结果看作时一个@开头的字符串返回
        if (result != null && result.startsWith("@@")) {
            return result.substring(1);
        }
        if (result != null && result.startsWith("@")) {
            result = getProperty(result.substring(1));
        }

        return result;
    }

    /**
     * 获取指定key的属性值。
     *
     * <p>
     *  属性值未找到时，返回指定的默认值。
     * </p>
     *
     * @param key 属性名
     * @param defaultValue 属性默认值
     * @return 指定属性名的属性值
     */
    public static String getProperty(String key, String defaultValue) {
        String result = props.get(key);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    /**
     * 获取追加属性文件的读取路径。
     *
     * 以属性文件存在的目录为基础，返回属性文件中附加文件的读取路径。
     *
     * @param resource 含有附加文件的属性文件
     * @param addFile 追加属性文件
     * @return 追加属性文件的读取路径
     */
    private static String getPropertiesPath(String resource, String addFile) {
        File file = new File(resource);
        String dir = file.getParent();
        if (dir != null) {
            StringBuilder dirBuf = new StringBuilder();
            dirBuf.setLength(0);
            dirBuf.append(dir);
            dirBuf.append(File.separator);
            dir = dirBuf.toString();
        } else {
            dir = "";
        }
        StringBuilder retBuf = new StringBuilder();
        retBuf.setLength(0);
        retBuf.append(dir);
        retBuf.append(addFile);
        return retBuf.toString();
    }
}

