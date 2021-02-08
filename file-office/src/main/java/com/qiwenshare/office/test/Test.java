package com.qiwenshare.office.test;


import com.qiwenshare.office.utils.AsposeUtil;

public class Test {

//    private static void testWord(String path_word, String pafpath) throws Exception {
//        String word1 = path_word + "01正方数字.docx";
//        String word2 = path_word + "02正方数字.docx";
//        String word3 = path_word + "03正方数字.doc";
//        String word4 = path_word + "04正方数字.doc";
//        String word5 = path_word + "05正方数字.docx";
//        String word6 = path_word + "06正方数字.doc";
//
//        OpenOfficeUtils.toPdf(word1, pafpath + "Open-word-01测试.pdf");
//        OpenOfficeUtils.toPdf(word2, pafpath + "Open-word-02测试.pdf");
//        OpenOfficeUtils.toPdf(word3, pafpath + "Open-word-03测试.pdf");
//        OpenOfficeUtils.toPdf(word4, pafpath + "Open-word-04测试.pdf");
//        OpenOfficeUtils.toPdf(word5, pafpath + "Open-word-05测试.pdf");
//        OpenOfficeUtils.toPdf(word6, pafpath + "Open-word-06测试.pdf");
//
//    }
//
//    private static void testWord2(String path_word, String pafpath) throws Exception {
//        String word1 = path_word + "01.docx";
//        String word2 = path_word + "02.docx";
//        String word3 = path_word + "03.doc";
//        String word4 = path_word + "04.doc";
//        String word5 = path_word + "05.docx";
//        String word6 = path_word + "06.doc";
//
//        OpenOfficeUtils.toPdf(word1, pafpath + "Open-word-01.pdf");
//        OpenOfficeUtils.toPdf(word2, pafpath + "Open-word-02.pdf");
//        OpenOfficeUtils.toPdf(word3, pafpath + "Open-word-03.pdf");
//        OpenOfficeUtils.toPdf(word4, pafpath + "Open-word-04.pdf");
//        OpenOfficeUtils.toPdf(word5, pafpath + "Open-word-05.pdf");
//        OpenOfficeUtils.toPdf(word6, pafpath + "Open-word-06.pdf");
//
//    }
//
//    private static void testTxt(String path_word, String pafpath) throws Exception {
//        String txt1 = path_word + "01jvm.txt";
//        String txt2 = path_word + "02jvm.txt";
//        String txt3 = path_word + "03jvm.txt";
//
//        OpenOfficeUtils.toPdf(txt1, pafpath + "Open-txt-01测试.pdf");
//        OpenOfficeUtils.toPdf(txt2, pafpath + "Open-txt-02测试.pdf");
//        OpenOfficeUtils.toPdf(txt3, pafpath + "Open-txt-03测试.pdf");
//    }
//
//    private static void testTxt2(String path_word, String pafpath) throws Exception {
//        String txt1 = path_word + "01jvm.txt";
//        String txt2 = path_word + "02jvm.txt";
//        String txt3 = path_word + "03jvm.txt";
//
//        OpenOfficeUtils.toPdf(txt1, pafpath + "Open-txt-01.pdf");
//        OpenOfficeUtils.toPdf(txt2, pafpath + "Open-txt-02.pdf");
//        OpenOfficeUtils.toPdf(txt3, pafpath + "Open-txt-03.pdf");
//    }
//
//    private static void testExcel(String path_word, String pafpath) throws Exception {
//        String txt1 = path_word + "01部门开发任务管理.xlsx";
//        String txt2 = path_word + "02部门开发任务管理.xlsx";
//        String txt3 = path_word + "03部门开发任务管理.xlsx";
//
//        OpenOfficeUtils.toPdf(txt1, pafpath + "Open-excel-01测试.pdf");
//        OpenOfficeUtils.toPdf(txt2, pafpath + "Open-excel-02测试.pdf");
//        OpenOfficeUtils.toPdf(txt3, pafpath + "Open-excel-03测试.pdf");
//    }
//
//    private static void testExcel2(String path_word, String pafpath) throws Exception {
//        String txt1 = path_word + "01.xlsx";
//        String txt2 = path_word + "02.xlsx";
//        String txt3 = path_word + "03.xlsx";
//
//        OpenOfficeUtils.toPdf(txt1, pafpath + "Open-excel-01.pdf");
//        OpenOfficeUtils.toPdf(txt2, pafpath + "Open-excel-02.pdf");
//        OpenOfficeUtils.toPdf(txt3, pafpath + "Open-excel-03.pdf");
//    }
//
//    private static void testPPt(String path_ppt, String pafpath) throws Exception {
//        String txt1 = path_ppt + "01jquery培训.pptx";
//        String txt2 = path_ppt + "02jquery培训.pptx";
//        String txt3 = path_ppt + "03jquery培训.ppt";
//
//        OpenOfficeUtils.toPdf(txt1, pafpath + "Open-ppt-01测试.pdf");
//        OpenOfficeUtils.toPdf(txt2, pafpath + "Open-ppt-02测试.pdf");
//        OpenOfficeUtils.toPdf(txt3, pafpath + "Open-ppt-03测试.pdf");
//    }
//
//    private static void testPPt2(String path_ppt, String pafpath) throws Exception {
//        String txt1 = path_ppt + "01jquery.pptx";
//        String txt2 = path_ppt + "02jquery.pptx";
//        String txt3 = path_ppt + "03jquery培训.ppt";
//
//        OpenOfficeUtils.toPdf(txt1, pafpath + "Open-ppt-01.pdf");
//        OpenOfficeUtils.toPdf(txt2, pafpath + "Open-ppt-02.pdf");
//        OpenOfficeUtils.toPdf(txt3, pafpath + "Open-ppt-03.pdf");
//    }
//
//    public static void LinuxTest() throws Exception {
//        String path_word = "/software/songyan/hah/01word/";
//        String path_txt = "/software/songyan/hah/02txt/";
//        String path_excel = "/software/songyan/hah/03excel/";
//        String path_ppt = "/software/songyan/hah/04ppt/";
//        String pafpath = "/software/songyan/hah/pdf/";
//
//        System.out.println("************************");
//        testTxt(path_txt, pafpath);
//        System.out.println("************************");
//        testExcel(path_excel, pafpath);
//        System.out.println("************************");
//        testPPt(path_ppt, pafpath);
//        System.out.println("************************");
//        testWord(path_word, pafpath);
//    }
//
//    public static void LinuxTest2() throws Exception {
//        String path_word = "/software/songyan/hah/01word/";
//        String path_txt = "/software/songyan/hah/02txt/";
//        String path_excel = "/software/songyan/hah/03excel/";
//        String path_ppt = "/software/songyan/hah/04ppt/";
//        String pafpath = "/software/songyan/hah/pdf/";
//
//        System.out.println("************************");
//        testTxt2(path_txt, pafpath);
//        System.out.println("************************");
//        testExcel2(path_excel, pafpath);
//        System.out.println("************************");
//        testPPt2(path_ppt, pafpath);
//        System.out.println("************************");
//        testWord2(path_word, pafpath);
//    }
//
//    public static void winTest() throws Exception {
//        String path_word = "C:/Users/Administrator.DESKTOP-QN9A3AA/Desktop/office/测试文档/转换前文档/01word/";
//        String path_txt = "C:/Users/Administrator.DESKTOP-QN9A3AA/Desktop/office/测试文档/转换前文档/02txt/";
//        String path_excel = "C:/Users/Administrator.DESKTOP-QN9A3AA/Desktop/office/测试文档/转换前文档/03excel/";
//        String path_ppt = "C:/Users/Administrator.DESKTOP-QN9A3AA/Desktop/office/测试文档/转换前文档/04ppt/";
//        String pafpath = "C:/Users/Administrator.DESKTOP-QN9A3AA/Desktop/office/测试文档/pdf/";
//
//        System.out.println("************************");
//        testWord(path_word, pafpath);
//        System.out.println("************************");
//        testTxt(path_txt, pafpath);
//        System.out.println("************************");
//        testExcel(path_excel, pafpath);
//        System.out.println("************************");
//        testPPt(path_ppt, pafpath);
//    }
    
    public static void main(String[] args) throws Exception {
        //AsposeUtil.trans("C:\\Users\\machaop\\Desktop\\jci操作手册.docx", "D:\\Downloads\\jci.pdf", "word");
        //AsposeUtil.trans("C:\\Users\\machaop\\Desktop\\ZDB_统一支付清算平台_接口说明书_V1.2-20210202.xlsx", "D:\\Downloads\\ZDB_统一支付清算平台_接口说明书_V1.2-20210202.pdf", "excel");
        AsposeUtil.trans("C:\\Users\\machaop\\Desktop\\Java10(企业级数据库之oracle安装).ppt", "D:\\Downloads\\Java10.pdf", "ppt");
//        winTest();
    }
}