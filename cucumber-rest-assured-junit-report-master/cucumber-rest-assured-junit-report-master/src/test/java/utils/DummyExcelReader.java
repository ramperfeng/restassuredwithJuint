package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import jxl.Sheet;
import jxl.Workbook;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.slf4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.*;

public class DummyExcelReader {

    public String path;
    public FileInputStream fis = null;
    public OutputStream fileOut = null;
    private XSSFWorkbook workbook = null;
    private XSSFSheet sheet = null;
    private XSSFRow row = null;
    private XSSFCell cell = null;

    private static ObjectMapper mapper=new ObjectMapper();
    public Logger logman;
    public static ThreadLocal<String> mandetroryorEmptyFieldValidate = new ThreadLocal<String>();

    public DummyExcelReader(String path) {
        this.path = path;
        try {
            fis = new FileInputStream(path);
            workbook = new XSSFWorkbook(fis);
            fis.close();
            logman.info("Excel XSSFWorkbook object is created successfully");
        } catch (Exception e) {
            logman.error("Error in creating object of excel file,Error=" + e.getMessage());
        }
    }

    public int getRowCount(String sheetName) {
        try {
            int index = workbook.getSheetIndex(sheetName);
            if (index == -1) {
                logman.info("invalid sheetname ,in getRow count method ");
                return 0;
            } else {
                sheet = workbook.getSheetAt(index);
                int number = sheet.getLastRowNum() + 1;
                logman.info("row number is returned successfully for sheet =" + sheetName + " as " + number);
                return number;
            }
        } catch (Exception e) {
            logman.error("Error in getRowCount method,Error=" + e.getMessage());
            return 0;
        }
    }

    public String getCellData(String sheetName, String colName, int rowNum) {
        try {
            if (rowNum <= 0)
                return "";
            int index = workbook.getSheetIndex(sheetName);
            int col_num = -1;
            if (index == -1)
                return "";
            sheet = workbook.getSheetAt(index);
            row = sheet.getRow(0);
            for (int i = 0; i < row.getLastCellNum(); i++) {
                if (row.getCell(i).getStringCellValue().trim().equals(colName.trim()))
                    col_num = i;
            }
            if (col_num == -1)
                return "";
            sheet = workbook.getSheetAt(index);
            row = sheet.getRow(rowNum - 1);
            if (row == null)
                return "";
            cell = row.getCell(col_num);
            if (cell == null)
                return "";
            if (cell.getCellType() == CellType.STRING)
                return cell.getStringCellValue();
            else if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
                String cellText = String.valueOf(cell.getNumericCellValue());
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    double d = cell.getNumericCellValue();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(HSSFDateUtil.getJavaDate(d));
                    cellText = (String.valueOf(cal.get(Calendar.YEAR)).substring(2));
                    cellText = cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.MONTH) + 1 + "/" + cellText;
                }
                return cellText;
            } else if (cell.getCellType() == CellType.BLANK)
                return "";
            else
                return String.valueOf(cell.getBooleanCellValue());


        } catch (Exception e) {
            logman.error("row" + rowNum + "or column" + colName + "does not exist in xls");
            return "row" + rowNum + "or column " + colName + "does not exist in xls";
        }

    }


    public static String readXlsJONFile(String payload, String inputFileName, String sheet) {
        /*Construct the paths for inputand output files */
        String excelFilePath = inputFileName + "\\" + inputFileName + ".xls";
        String inputFilePath = inputFileName + "\\" + inputFileName + "Input.txt";
        String outputFilePath = inputFileName + "\\" + inputFileName + "Output.txt";

        String fullReqPayload = returnInputPayloadWithList(payload, excelFilePath, inputFilePath, outputFilePath, sheet);


        /*Construct the (Payload with List of Elements*/


        if (fullReqPayload.contains("[$-{")) {

            if (fullReqPayload.contains("[$-{ListOFElementa)}")) {

                inputFilePath = inputFileName + "\\" + inputFileName + "ListInput.txt";
                outputFilePath = inputFileName + "\\" + inputFileName + "ListInput.txt";
                sheet = "LIST";

                String listReqPayload = returnInputPayloadWithList(payload, excelFilePath, inputFilePath, outputFilePath, sheet);
                fullReqPayload = fullReqPayload.replace("\"{$-ListOfElements}]\" ", "[" + listReqPayload + "]");
            }
            if (fullReqPayload.contains("[$-{ChildElements}]")) {

                inputFilePath = inputFileName + "\\" + inputFileName + "ChildListInput.txt";
                outputFilePath = inputFileName + "\\" + inputFileName + "ChildListInput.txt";
                sheet = "CHILDLIST";

                String childListReqPayload = returnInputPayloadWithList(payload, excelFilePath, inputFilePath, outputFilePath, sheet);
                fullReqPayload = fullReqPayload.replace("\"{$-ChildListOFElements]]\" ", "[" + childListReqPayload + "]");

            }
        }

        if (DummyExcelReader.mandetroryorEmptyFieldValidate.get() != "Y") {
            fullReqPayload = removeEmptyNodeFromJsonPayload(fullReqPayload);
        }
        //fullReqPayload = fullReqPayload.replace("EMPTY", "");
        return fullReqPayload;
    }


    public static String removeEmptyNodeFromJsonPayload(String JsonPayloadInString)
    {
        String outputPayloadAfterEmptyBlockremoved="";
        try
        {
            JsonNode InputInJsonNode=getExampleRoot(JsonPayloadInString);
            ObjectNode objectNode= InputInJsonNode.deepCopy();
            ObjectNode result=removeEmptyFields(objectNode);

            for (int i=0;i<3;i++)
            {
                result=removeEmptyFields(result);
            }
            outputPayloadAfterEmptyBlockremoved=mapper.writeValueAsString(result);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return outputPayloadAfterEmptyBlockremoved;
    }

    public static  ObjectNode removeEmptyFields(final ObjectNode jsonNode)
    {
        ObjectNode ret=new ObjectMapper().createObjectNode();
        Iterator<Map.Entry<String,JsonNode>> iter=jsonNode.fields();
        while ( (iter.hasNext()))
        {
            Map.Entry<String,JsonNode> entry=iter.next();
            String key=entry.getKey();
            JsonNode value=entry.getValue();
            if(value instanceof ObjectNode)
            {
                Map<String,ObjectNode> map =new HashMap<String,ObjectNode>();
                if(!((value.isEmpty()|| value.isNull())))
                {
                    map.put(key,removeEmptyFields((ObjectNode) value));
                    ret.setAll(map);
                }

            }else if(value instanceof  ArrayNode)
            {
                if(!((value.isEmpty()|| value.isNull())))
                {
                    ret.set(key,removeEmptyFields((ArrayNode) value));

                }
            }
            else if (value.asText() !=null && ! value.asText().isEmpty())
            {
                ret.set(key,value);
            }
        }
        return ret;
    }

    // Remove empty fields from the given json array node

    public static ArrayNode removeEmptyFields(ArrayNode array)
    {
        ArrayNode ret=new ObjectMapper().createArrayNode();
        Iterator<JsonNode> iter=array.elements();
        while (iter.hasNext())
        {
            JsonNode value=iter.next();
            if(value instanceof  ArrayNode)
            {
                ret.add(removeEmptyFields((ArrayNode) (value)));

            }
            else if (value instanceof ObjectNode)
            {
                ret.add(removeEmptyFields((ObjectNode) (value)));

            }
            else if (value !=null && !value.textValue().isEmpty())
            {
                ret.add(value);
            }
        }
        return  ret;
    }
    public static  JsonNode getExampleRoot(String inputJson) throws IOException
    {
        InputStream exampleInput=new ByteArrayInputStream(inputJson.getBytes(Charset.forName("UTF-8")));
        JsonNode rootNode=mapper.readTree(exampleInput);
        return  rootNode;
    }

    public static String jsonFormat(String jsonstring) {
        String jstring = jsonstring.trim();
        JsonReader reader = new JsonReader(new StringReader(jstring));
        reader.setLenient(true);
        JsonParser jsonParser = new JsonParser();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement element = jsonParser.parse(reader);
        jstring = gson.toJson(element);
        return jstring;

    }


    //return true is sheet is creaed successfully else false
    public boolean addSheet(String sheetname) {
        FileOutputStream fileOut;
        try {
            workbook.createSheet(sheetname);
            fileOut = new FileOutputStream(path);
            workbook.write(fileOut);
            fileOut.close();
            logman.info("sheet ,=" + sheetname + "added successfully");
        } catch (Exception e) {
            logman.error("Error in addsheet method ,Error =" + e.getMessage());
            return false;
        }
        return true;
    }

    //return true if sheet is removed successfully else false if sheet does not exist
    public boolean removeSheet(String sheetName) {
        try {
            int index = workbook.getSheetIndex(sheetName);
            if (index == -1)
                return false;
            FileOutputStream fileOut;
            workbook.removeSheetAt(index);
            fileOut = new FileOutputStream(path);
            workbook.write(fileOut);
            fileOut.close();
            logman.info("Sheet ,=" + sheetName + "removed successfully");
        } catch (Exception e) {
            logman.error("Error in addSheet method,Error=" + e.getMessage());
            return false;
        }
        return true;
    }



    public static String returnInputPayloadWithList(String payload,String excelFilePath,String inputFilePath,String outputFilePath,String sheet)
    {
        System.getProperty("user.dir");
        Workbook tcWorkBook;
        String outputFileName="";
        String constantPath="/src/test/resources/payload/";
        String excelPath=constantPath+excelFilePath;
        ArrayList<String> keysToRemove= new ArrayList<> ();

        if((sheet=="DATA")||(sheet=="LIST")||(sheet=="CHILDLIST"))
        {
            outputFileName=constantPath+inputFilePath;
        }
        else if(sheet=="RESPONSE")
        {
            outputFileName=constantPath+outputFilePath;
        }
        else
        {
            System.out.println("ERROR :Sheet name provided is "+sheet + "should be DATA or RESPONSE");
        }

        String sheet_Path=System.getProperty("user.dir")+excelPath;
        String outfilePath=System.getProperty("user.dir")+outputFileName;
        String key_Name="";
        String requestPayload="";
        String interimReqPayload="";
        try
        {
            File file1=new File(sheet_Path);
            tcWorkBook= Workbook.getWorkbook(file1);
            Sheet tcSheet=tcWorkBook.getSheet(sheet);
            System.out.print(tcSheet.getRows());
            int key_nameReturns=0;
            /* Reading the excel to get data */
            for(int row=1;row<tcSheet.getRows();row++)
            {

                key_Name=tcSheet.getCell(0,row).getContents();
                if(payload.equalsIgnoreCase(key_Name))
                {

                    key_nameReturns++;
                    String lines="";
                    String line;
                    /* Reading the Json template from specified Path */
                    try
                    {
                        File file = new File(outfilePath);
                        BufferedReader reader= new BufferedReader(new FileReader(file.toString()));
                        while((line =reader.readLine())!=null)
                        {
                            lines=lines+line;

                        }
                        reader.close();
                    }catch (Exception e)
                    {

                    }
                    lines=lines.trim().replaceAll("\\s+","");

                    /* Inserting data in JSON Payload  */

                    for ( int col=1;col<tcSheet.getColumns();col++)
                    {
                        String colHeader=tcSheet.getCell(col,0).getContents();
                        String colHeaderVal= "$-{"+colHeader+"}";
                        String colValue=tcSheet.getCell(col,row).getContents();
                        //Removing Field with "NA" from Json Payload &  remove empty nodes from Json Payload

                        if (colValue.equalsIgnoreCase("NA"))
                        {
                            String testfield="\""+colHeader+"\""+colHeaderVal+"\"";
                            if(lines.contains(testfield+","))
                            {
                                testfield=testfield+",";
                            }
                            else if (lines.contains(","+testfield))
                            {
                                testfield=","+testfield;
                            }
                            lines=lines.replace(testfield,"");
                        }else
                        {
                            lines=lines.replace(colHeaderVal,colValue);
                        }
                    }
                    if(key_nameReturns>1)
                        requestPayload=requestPayload+","+lines;
                    else
                        requestPayload=lines;
                }

            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return requestPayload;


    }
    public static boolean compareJson(String expectedResponsePayload,String actualResponsePayload)
    {
        boolean matched=true;
        try
        {
            Gson g=new Gson();
            Type mapType=new TypeToken<Map<String,Object>>()
            {

            }.getType();
            Map<String,Object>firstMap=g.fromJson(expectedResponsePayload, mapType);
            Map<String,Object>secondMap=g.fromJson(actualResponsePayload, mapType);
            Map<String,Object> leftFlatMap=FlatMapUtil.flatten(firstMap);
            Map<String,Object> rightFlatMap=FlatMapUtil.flatten(secondMap);
            MapDifference<String,Object> difference=Maps.difference(leftFlatMap, rightFlatMap);
            if(difference.entriesDiffering().size()> 0)
            {
                matched=false;
                System.out.println("\n\n No Difference \n------------------------------------");
                //basePage.injectMessageToCucumberReport("\n\n No Difference \n----------------);
                difference.entriesDiffering().forEach((key,value)-> System.out.println(key+":"+value+"\n -------------------"));
            }


        }catch(Exception e)
        {
            System.out.println("\n\n No Difference \n------------------------------------");
            //basePage.injectMessageToCucumberReport("\n\n No Difference \n----------------);
        }
        return matched;
    }

    public static void readMultipleRecordsXlsJSONFile(String payload,String inputFileName,String sheet,String responsePayload)
    {
        System.getProperty("user.dir");
        Workbook tcWorkBook;
        String outputFileName="";
        String constantPath="\\src\\test\\resources\\payload\\";
        String excelFilePath=inputFileName+"\\"+inputFileName+".xls";
        String inputFilePath=inputFileName+"\\"+inputFileName+"Input.txt";
        String outputFilePath=inputFileName+"\\"+inputFileName+"Output.txt";
        String excelPath=constantPath+excelFilePath;

        if(sheet=="DATA")
        {
            outputFileName=constantPath+inputFilePath;
        }else if(sheet=="RESPONSE")
        {
            outputFileName=constantPath+outputFilePath;

        }
        else
        {
            System.out.println("Error :sheet name provided is  not available :"+sheet+ "it should be DATA or RESPONSE");

        }
        String sheet_Path=System.getProperty("user.dir")+excelPath;
        String outfilepath=System.getProperty("user.dir")+outputFileName;
        String expectedResponseBlock="";
        String key_Name="";
        int responseBlockList=0;
        try
        {
            File file1=new File(sheet_Path);
            tcWorkBook=Workbook.getWorkbook(file1);
            Sheet tcSheet=tcWorkBook.getSheet(sheet);
            System.out.print(tcSheet.getRows());

            String line;
            String lines="";
            try
            {
                File file=new File(outfilepath);
                BufferedReader reader=new BufferedReader(new FileReader(file.toString()));
                while ((line=reader.readLine())!=null)
                {
                    lines=lines+line;

                }
                reader.close();
            }catch (Exception e)
            {

            }

            /* Reading the Excel to get data */
            for(int row=1;row <tcSheet.getRows();row++)
            {
                key_Name=tcSheet.getCell(0,row).getContents();
                if(payload.equalsIgnoreCase(key_Name))
                {
                    expectedResponseBlock=lines;
                    responseBlockList++;
                    //basepPage.injectErrorToCucumberReport("block list"+responseBlockList);
                    /* Inserting data in Json payload */
                    for(int col=1;col<tcSheet.getColumns();col++)
                    {
                        String colHeader=tcSheet.getCell(col,0).getContents();
                        String colHeaderVal="$-{"+colHeader+"}";
                        String colValue=tcSheet.getCell(col,row).getContents();
                        if(colValue.equalsIgnoreCase("NA"))
                        {
                            String expectedfield="\""+colHeader+"\""+":\"$-{"+colHeader+"}\"";
                            if(expectedResponseBlock.contains(expectedfield+";"))
                            {
                                expectedfield=expectedfield+",";

                            }
                            else if (expectedResponseBlock.contains(","+expectedfield))
                            {
                                expectedfield=","+expectedfield;
                            }
                            expectedResponseBlock=expectedResponseBlock.replace(expectedfield,"");

                        }
                        else
                        {
                            expectedResponseBlock=expectedResponseBlock.replace(colHeaderVal,colValue);
                        }
                    }
                    expectedResponseBlock=removeEmptyNodeFromJsonPayload(expectedResponseBlock);
                    expectedResponseBlock=expectedResponseBlock.replace("EMPTY","");
                   // basepPage.injectMessageToCucumberReport("expected response from record number -"+responseBlockList+" : "+jsonFormat(expectedResponseBlock));
                    Assert.assertTrue("unexpected response from service  "+responseBlockList+" :"+expectedResponseBlock,compareJson((expectedResponseBlock
                            .replace("\\s+","")),(responsePayload.replaceAll("\\s+",""))));
                }
            }
            Assert.assertTrue("Response key not matched :",responseBlockList !=0);
        }catch (Exception e)
        {
            e.printStackTrace();
            Assert.assertTrue("Unexpected error happened due to : "+e.getMessage(),false);
        }
    }


}
