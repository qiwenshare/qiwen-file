package com.qiwenshare.office.utils;

import java.io.*;

import com.aspose.cells.Workbook;
import com.aspose.slides.Presentation;
import com.aspose.words.*;


public class AsposeUtil {

    //校验license
    private static boolean judgeLicense() {
        boolean result = false;
        try {
            InputStream is = AsposeUtil.class.getClassLoader().getResourceAsStream("license.xml");
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 转换
    public static void trans(String filePath, String pdfPath, String type) {
        if (!judgeLicense()) {
            System.out.println("license错误");
        }
        try {
            System.out.println("as开始：" + filePath);
            long old = System.currentTimeMillis();
            File file = new File(pdfPath);
            toPdf(file, filePath, type);
            long now = System.currentTimeMillis();
            System.out.println("完成：" + pdfPath);
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒");
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    private static void toPdf(File file, String filePath, String type) {
        if ("word".equals(type) || "txt".equals(type)) {
            wordofpdf(file, filePath);
        } else if ("excel".equals(type)) {
            exceOfPdf(file, filePath);
        } else if ("ppt".equals(type)) {
            pptofpdf(file, filePath);
        }else{
            System.out.println("暂不支持该类型："+type);
        }
    }

    private static void wordofpdf(File file, String filePath) {
        FileOutputStream os = null;
        Document doc;
        try {
            os = new FileOutputStream(file);
            doc = new Document(filePath);
            doc.save(os, com.aspose.words.SaveFormat.PDF);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void exceOfPdf(File file, String filePath) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            Workbook wb = new Workbook(filePath);
            wb.save(os, com.aspose.cells.SaveFormat.PDF);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//
    private static void pptofpdf(File file, String filePath) {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            Presentation pres = new Presentation(filePath);// 输入pdf路径
            pres.save(os, com.aspose.slides.SaveFormat.Pdf);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}