package com.summerrc.com.video;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class StreamTool {

    public static void save(File file, byte[] data) throws Exception {
        FileOutputStream outStream = new FileOutputStream(file);
        outStream.write(data);
        outStream.close();
    }

    /**
     * 创建IPConfig文件
     *
     * @param ip ip
     */
    public static void createFile(String ip) {
        File file = new File(Environment.getExternalStorageDirectory(), "IpConfig.txt");
        byte[] ipbyte = ip.getBytes();
        try {
            save(file, ipbyte);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String readLine(PushbackInputStream in) throws IOException {
        char buf[] = new char[128];
        int room = buf.length;
        int offset = 0;
        int c;
        loop:
        while (true) {
            switch (c = in.read()) {
                case -1:
                case '\n':
                    break loop;
                case '\r':
                    int c2 = in.read();
                    if ((c2 != '\n') && (c2 != -1))
                        in.unread(c2);
                    break loop;
                default:
                    if (--room < 0) {
                        char[] lineBuffer = buf;
                        buf = new char[offset + 128];
                        room = buf.length - offset - 1;
                        System.arraycopy(lineBuffer, 0, buf, 0, offset);

                    }
                    buf[offset++] = (char) c;
                    break;
            }
        }
        if ((c == -1) && (offset == 0))
            return null;
        return String.copyValueOf(buf, 0, offset);
    }

    /**
     * 读取流
     *
     * @param inStream  inStream
     * @return 字节数组
     * @throws Exception
     */
    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }

    public static String readIP() throws IOException {
        File file = new File(Environment.getExternalStorageDirectory(), "IpConfig.txt");
        BufferedReader in = new BufferedReader(new FileReader(file));

        String ipconfig = in.readLine();
        System.out.println(ipconfig);

        return ipconfig;
    }
}
