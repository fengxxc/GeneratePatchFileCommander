package app;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {

    // 日志
    public static StringBuffer LOG = new StringBuffer();
    // 系统换行符
    public static String LINE_SEP = System.lineSeparator();

    public static void main(String[] args) {
        LOG.append(LINE_SEP + "======== " + getTimeFormat() + " 给我生 <(￣︶￣)↗[GO!] ========" + LINE_SEP);
        Properties config = null;
        try {
            config = Util.loadConfig("E:\\github\\patchFileGenerater\\patchFileGenerater-java\\src\\app\\config.properties");
        } catch (FileNotFoundException e) {
            LOG.append("error: 未找到配置文件" + LINE_SEP);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (config == null) {
            LOG.append("error: 解析配置文件出错 (ノへ￣、)" + LINE_SEP);
            LOG.append("======== " + getTimeFormat() + " 生...生不出来惹 _(:з)∠)_ ========" + LINE_SEP);
            return;
        }
        copyFileRecursive(config);
        String dirtree = Util.getDirList(config.getProperty("targetPath"), "", LINE_SEP);
        LOG.append("最后生成的文件目录如下：" + LINE_SEP);
        LOG.append(dirtree + LINE_SEP);
        LOG.append("======== " + getTimeFormat() + " 哦呼，擦汗 ^o^y ========" + LINE_SEP);
        String log = config.getProperty("log");
        if (log != null && !"".equals(log) && "true".equals(log)) {
            saveLog(LOG.toString(), config.getProperty("logPath"));
            LOG.append("日志已保存");
        }
        System.out.println(LOG.toString());
        // Press <Enter> to Esc

    }

    private static void saveLog(String log, String path) {
        Util.mkDirs(path);
        File f = new File(path + getDateFormat() + ".txt");
        FileWriter fw = null;
        try {
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        // pw.print(log);
        pw.write(log);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyFileRecursive(Properties config) {
        boolean isPathFromFile = "true".equals(config.getProperty("patchTxtFromFile")) ? true : false;
        String[] workPaths;
        if (isPathFromFile) {
            workPaths = getWorkPathByPatchTxt(config.getProperty("patchTxtPath"));
        } else {
            workPaths = getWorkPathByClipboard();
        }
        String[] sourcePath = config.getProperty("sourcePaths").split(",\\s*");
        String outputPath = config.getProperty("outputPath");
        String targetPath = config.getProperty("targetPath");
        String projectPath = config.getProperty("projectPath");
        for (int i = 0; i < workPaths.length; i++) {
            String finalPath = workPaths[i];
            String sourceMatch = isArrMatchStr(sourcePath, finalPath);
            if (sourceMatch != null) {
                // 源码路径替换成编译路径
                finalPath = finalPath.replaceFirst(sourceMatch, outputPath);
                // 文件扩展名替换
                finalPath = finalPath.replace(".java", ".class");
            }
            String finalAbsPath = Util.pathJoin(targetPath, Util.getFilePath(finalPath));
            // 创建不存在的目录
            Util.mkDirs(finalAbsPath);
            // 复制文件
            String sourceFilename = Util.pathJoin(projectPath, finalPath);
            try {
                Util.copyFile(sourceFilename, finalAbsPath);
            } catch (FileNotFoundException e) {
                LOG.append("未找到文件: " + sourceFilename + LINE_SEP);
                e.printStackTrace();
                continue;
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            LOG.append("已复制：'" + sourceFilename + "'" + LINE_SEP + "    至：'" + finalAbsPath + "'" + LINE_SEP);
            LOG.append("-------------------------------------" + LINE_SEP);
        }
    }

    /**
     * 数组arr中的元素有在str中的吗？ 在，就返回这个元素；不在，就返回空
     */
    private static String isArrMatchStr(String[] arr, String str) {
        for (int i = 0; i < arr.length; i++)
            if (str.contains(arr[i]))
                return arr[i];
        return null;
    }

    /**
     * 读取patch中的补丁文件路径从剪贴板
     */
    private static String[] getWorkPathByClipboard() {
        String cbTxt = "";
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable ctf = cb.getContents(null);
        if (ctf != null) {
            // 检查内容是否是文本类型
            if (ctf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    cbTxt = (String) ctf.getTransferData(DataFlavor.stringFlavor);
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        List<String> workPath = new ArrayList<String>();
        // Pattern p = Pattern.compile("Index:\\.+" + LINE_SEP, Pattern.MULTILINE);
        Pattern p = Pattern.compile("Index:.*"); // 匹配从"Index:"至行尾
        Matcher m = p.matcher(cbTxt);
        while (m.find()) {
            workPath.add(m.group().replace("Index:", "").trim().replaceAll(LINE_SEP, "").replaceAll("\r", ""));
            // workPath.add(m.group());
            // System.out.println(m.group());
        }
        // System.out.println("***********************************");
        return workPath.toArray(new String[workPath.size()]);
    }

    /**
     * 读取patch中的补丁文件路径从文件
     */
    private static String[] getWorkPathByPatchTxt(String path) {
        File f = new File(path);
        List<String> workPath = new ArrayList<String>();
        try {
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Index:")) {
                    workPath.add(line.replace("Index:", "").trim().replaceAll(LINE_SEP, ""));
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workPath.toArray(new String[workPath.size()]);
    }

    private static String getTimeFormat() {
        return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
    }

    private static String getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }
}