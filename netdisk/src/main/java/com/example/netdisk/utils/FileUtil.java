package com.example.netdisk.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author monody
 * @date 2022/4/27 7:12 下午
 */
public class FileUtil {

    private static Map<String, Set<String>> map;

    static {
        map = new HashMap<>();
        Set<String> photo = new HashSet<>();
        photo.add("jpg");
        photo.add("jpeg");
        photo.add("png");
        photo.add("bmp");
        photo.add("gif");
        photo.add("ico");
        photo.add("heic");
        photo.add("webp");
        photo.add("svg");

        map.put("photo", photo);

        Set<String> music = new HashSet<>();
        music.add("mp3");
        music.add("wav");
        music.add("ogg");
        music.add("flac");
        music.add("ape");
        map.put("music", music);

        Set<String> document = new HashSet<>();
        document.add("doc");
        document.add("docx");
        document.add("xls");
        document.add("xlsx");
        document.add("ppt");
        document.add("pptx");
        document.add("txt");
        document.add("pdf");
        document.add("md");
        document.add("xmind");
        map.put("document", document);

        Set<String> compressed = new HashSet<>();
        compressed.add("zip");
        compressed.add("rar");
        compressed.add("gz");
        compressed.add("7z");
        compressed.add("jar");
        compressed.add("war");
        map.put("compressed", compressed);

        Set<String> video = new HashSet<>();
        video.add("mp4");
        video.add("rmvb");
        video.add("flv");
        video.add("avi");
        video.add("3gp");
        video.add("wmv");
        video.add("mov");
        map.put("video", video);

        Set<String> application = new HashSet<>();
        application.add("exe");
        application.add("msi");
        application.add("app");
        application.add("apk");
        application.add("rpm");
        application.add("deb");
        map.put("application", application);

        Set<String> mirror = new HashSet<>();
        mirror.add("dmg");
        mirror.add("img");
        mirror.add("iso");
        mirror.add("gho");
        map.put("mirror", mirror);

        Set<String> code = new HashSet<>();
        code.add("java");
        code.add("c");
        code.add("b");
        code.add("cpp");
        code.add("h");
        code.add("hpp");
        code.add("py");
        code.add("sh");
        code.add("js");
        code.add("css");
        code.add("html");
        code.add("json");
        code.add("sql");
        code.add("go");
        code.add("php");
        code.add("jsp");
        code.add("asp");
        code.add("aspx");
        code.add("jspx");
        code.add("class");
        code.add("vbs");
        code.add("bat");
        code.add("shtml");
        code.add("htm");
        // Verilog 文件
        code.add("v");
        // VHDL 文件
        code.add("vhd");
        // C# 文件
        code.add("cs");
        map.put("code", code);

        Set<String> configuration = new HashSet<>();
        configuration.add("ini");
        configuration.add("conf");
        configuration.add("properties");
        configuration.add("yml");
        configuration.add("yaml");
        configuration.add("xml");
        // idea 配置文件
        configuration.add("imi");
        configuration.add("gitignore");

        map.put("configuration", configuration);

    }

    /**
     * 获取文件扩展名 （截取最后一个'.'后的内容）
     *
     * @param filename 字符串
     * @return 文件扩展名，不存在返回null
     */
    public static String getExt(String filename) {
        int index = filename.lastIndexOf('.');
        if (index == -1) {
            return null;
        }
        return filename.substring(index + 1);
    }

    public static String getType(String filename) {
        String ext = getExt(filename);
        String type = null;
        if (ext != null)
            ext = ext.toLowerCase();
        for (String key : map.keySet()) {
            HashSet<String> set = (HashSet<String>) map.get(key);
            if (set.contains(ext)) {
                type = key;
                break;
            }
        }
        return type;
    }

    /**
     * 获取一个文件的md5值(可处理大文件)
     *
     * @return md5 value
     */
    public static String getMD5(File file) {
        FileInputStream fileInputStream = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                md5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(md5.digest()));
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 求一个字符串的md5值
     *
     * @param target 字符串
     * @return md5 value
     */
    public static String toMD5(String target) {
        return DigestUtils.md5Hex(target);
    }

    public static void main(String[] args) {
        String ext = getExt("poptxt.");
        System.out.println(ext);
    }
}
