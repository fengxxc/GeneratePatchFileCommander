package app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Util
 */
public class Util {

    // 路径分隔用"/"，因为SVN diff导出的路径是"/"
    public static String SEPARATOR = "/";

    /**
     * 载入properties文件
     */
    public static Properties loadConfig(String path) throws FileNotFoundException, IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream(path));
        return prop;
    }

    /**
     * 文件拷贝，自动创建不存在路径
     */
    public static void copyFile(String sourceFilename, String finalAbsPath) throws FileNotFoundException, IOException {
        String newFilename = sourceFilename.substring(sourceFilename.lastIndexOf(SEPARATOR) + 1);
        String finalAbsFilename = finalAbsPath + SEPARATOR + newFilename;

        FileInputStream fis = new FileInputStream(sourceFilename);
        FileOutputStream fos = new FileOutputStream(new File(finalAbsFilename));
        byte datas[] = new byte[1024 * 8];
        int len = 0;
        while ((len = fis.read(datas)) != -1) {
            fos.write(datas, 0, len);
        }
        fis.close();
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 根据路径创建目录
     */
    public static void mkDirs(String path) {
        File f = new File(path);
        if (!(f.exists() && f.isDirectory())) 
            f.mkdirs();
    }

    /**
     * 路径拼接
     */
    public static String pathJoin(String targetPath, String filePath) {
        String p = "";
        if (targetPath.endsWith(SEPARATOR) || filePath.startsWith(SEPARATOR)) 
            p = targetPath + filePath;
        else 
            p = targetPath + SEPARATOR + filePath;
        return p;
    }
    /**
     * 去掉文件路径中的文件名
     * "D:/test/test.py" -> "D:/test/"
     */
    public static String getFilePath(String path) {
        // int index = path.lastIndexOf(SEPARATOR);
         // 路径分隔用"/"，因为SVN diff导出的路径是"/"
        int index = path.lastIndexOf(SEPARATOR);
        return path.substring(0, index+1);
    }

    /**
     * 生成目录树字符串
     */
	public static String getDirList(String path, String placeholder, String lineSeparator) {
        String BRANCH = "├─ ";
        String LAST_BRANCH = "└─ ";
        String TAB = "│  ";
        String EMPTY_TAB = "   ";
        ArrayList<String> foldList = new ArrayList<String>();
        ArrayList<String> fileList = new ArrayList<String>();
        File f = new File(path);
        File[] fs = f.listFiles();
        for (int i = 0; i < fs.length; i++) {
            if (fs[i].isDirectory()) {
                foldList.add(fs[i].getName());
            } else if (fs[i].isFile()) {
                fileList.add(fs[i].getName());
            }
        }
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < foldList.size()-1; i++) {
            result.append(placeholder + BRANCH + foldList.get(i) + lineSeparator);
            result.append(getDirList(pathJoin(path, foldList.get(i)), placeholder + TAB, lineSeparator));
        }
        if (foldList.size() > 0) {
            String joint = fileList.size() > 0 ? BRANCH : LAST_BRANCH;
            result.append(placeholder + joint + foldList.get(foldList.size()-1) + lineSeparator);
            String joint2 = fileList.size() > 0 ? TAB : EMPTY_TAB;
            result.append(getDirList(pathJoin(path, foldList.get(foldList.size()-1)), placeholder + joint2, lineSeparator));
        }
        for (int i = 0; i < fileList.size()-1; i++) {
            result.append(placeholder + BRANCH + fileList.get(i) + lineSeparator);
        }
        if (fileList.size() > 0) {
            result.append(placeholder + LAST_BRANCH + fileList.get(fileList.size()-1) + lineSeparator);
        }
        return result.toString();
    }
    
    public static void main(String[] args) {
        System.out.println(getDirList("E:\\github\\patchFileGenerater\\dev-python", "*", System.lineSeparator()));
    }
}