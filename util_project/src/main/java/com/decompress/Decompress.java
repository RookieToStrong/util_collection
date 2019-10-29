package com.decompress;

import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 功能说明:
 *
 * @author maonb
 */
public class Decompress {

    public static void unzip(String sourceZip, String destDir){
        System.out.println("decompress begin");
        sourceZip = sourceZip.replace("\\","/");
        if(destDir == null || "".equals(destDir)){
            destDir = sourceZip.substring(0,sourceZip.lastIndexOf("/"));
        }
        Project project = new Project();
        Expand expand = new Expand();
        expand.setProject(project);
        expand.setSrc(new File(sourceZip));
        expand.setOverwrite(true);
        expand.setDest(new File(destDir));
        //zip解压默认是utf-8，而rar解压默认是gbk或gb2312，先统一都设置成gbk
        expand.setEncoding("GBK");
        expand.execute();
        System.out.println("decompress end");
    }

    public static List<String> decompressZip(String sourceZip, String destDir) throws IOException {
        System.out.println("decompress zip file begin");
        List<String> reList = new ArrayList<>();
        sourceZip = sourceZip.replace("\\","/");
        if(destDir == null || "".equals(destDir)){
            destDir = sourceZip.substring(0,sourceZip.lastIndexOf("/"));
        }
        FileOutputStream out = null;
        InputStream in = null;
        ZipFile zipFile = new ZipFile(sourceZip);
        Enumeration en = zipFile.entries();
        while (en.hasMoreElements()){
            ZipEntry zipEntry = (ZipEntry) en.nextElement();
            if(!zipEntry.isDirectory()){
                try{
                    in = zipFile.getInputStream(zipEntry);
                    String fileName = zipEntry.getName();
                    String destPath = destDir + File.separator + fileName;
                    destPath = destPath.replace("\\","/");
                    out = new FileOutputStream(new File(destPath));
                    int len = 0;
                    byte[] bytes = new byte[1000];
                    while((len = in.read(bytes)) > 0){
                        out.write(bytes,0,len);
                    }
                    reList.add(destPath);
                } finally {
                    if(in != null){
                        in.close();
                    }
                }
            }else{
                String fileName = zipEntry.getName();
                String destPath = destDir + File.separator + fileName;
                File f = new File(destPath);
                if(!f.exists()){
                    f.mkdirs();
                }
            }
        }
        System.out.println("decompress zip file end");
        return reList;
    }

    public static List<String> unRar(String sourceRar, String destDir){
        System.out.println("Decompress rar file begin");
        List<String> reList = new ArrayList<>();
        sourceRar = sourceRar.replace("\\","/");
        if(destDir == null || "".equals(destDir)){
            destDir = sourceRar.substring(0,sourceRar.lastIndexOf("/"));
        }
        Archive archive = null;
        InputStream in = null;
        FileOutputStream out = null;
        try {
            archive = new Archive(new FileInputStream(new File(sourceRar)));
            FileHeader fh = archive.nextFileHeader();
            while(fh != null){
                if(!fh.isDirectory()){
                    try{
                        String fileName = fh.getFileNameW().isEmpty() ? fh.getFileNameString() : fh.getFileNameW();
                        String fileDir = destDir + File.separator + fileName;
                        fileDir = fileDir.replace("\\","/");
                        File file = new File(fileDir);
                        if(!file.getParentFile().exists()){
                            file.getParentFile().mkdirs();
                        }
                        out = new FileOutputStream(file);
                        archive.extractFile(fh,out);
                        reList.add(fileDir);
                    } finally {
                        if(out != null){
                            out.close();
                        }
                    }
                }
                fh = archive.nextFileHeader();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("Decompress rar file end");
        return reList;
    }

    public static void main(String[] args) {
        try {
            List<String> list = decompressZip("D:\\测试\\压缩文件\\20191011.zip",null);
            list.stream().forEach(a-> System.out.println(a));
            List<String> rarList = unRar("D:\\测试\\压缩文件\\20191011.rar",null);
            rarList.stream().forEach(a-> System.out.println(a));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
