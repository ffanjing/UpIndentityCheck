import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.text.NumberFormat;

public class UpIdentityCheck {

    public static void main(String args[]) {
        String srcFile = "";
        String destFile = "D:\\up_identity_check_java.sql";
        String bankNum = "6501";
        String menuId = "YF";

        Workbook wb = getWorkBook(srcFile);
        Sheet sheet = wb.getSheetAt(0);

        writeFile(sheet, destFile,bankNum,menuId);

    }


    public static Workbook getWorkBook(String srcFile) {

        FileInputStream in = null;
        try {
            in = new FileInputStream(srcFile);
            Workbook wk = StreamingReader.builder()
                    //缓存到内存中的行数，默认是10
                    .rowCacheSize(100)
                    //读取资源时，缓存到内存的字节大小，默认是1024
                    .bufferSize(4096)
                    //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件
                    .open(in);
            return wk;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void writeFile(Sheet sheet, String destFile,String bankNum,String menuId) {
        int total = sheet.getLastRowNum();
        System.out.println("有效行数为:" + (total + 1));
        File file = new File(destFile);
        int i = 0;
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (Row row : sheet) {
                i++;
                Cell cell = row.getCell(0);
                if (cell.getStringCellValue().length() != 11) {
                    System.out.println(cell.getStringCellValue() + " : 手机号长度不为11位");
                } else {
                    String temp = "INSERT INTO up_identity_check VALUES ('" + menuId +  "','" +
                            bankNum + "','" + menuId + "','9090','" +
                            cell.getStringCellValue() + "','','','','','','','','','','','','');";
                    bw.write(temp);
                    if (i%1000 == 0){
                        bw.newLine();
                        bw.write("COMMIT;");
                    }
                    bw.newLine();
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    numberFormat.setMaximumFractionDigits(2);
                    System.out.println(numberFormat.format((float) i / (float) total * 100));
                }
            }
            bw.write("COMMIT;");
            bw.close();
            System.out.println("done!");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
