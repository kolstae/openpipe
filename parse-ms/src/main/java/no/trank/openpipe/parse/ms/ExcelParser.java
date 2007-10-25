package no.trank.openpipe.parse.ms;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import no.trank.openpipe.parse.api.ParseData;
import no.trank.openpipe.parse.api.Parser;
import no.trank.openpipe.parse.api.ParserException;
import no.trank.openpipe.parse.api.ParserResult;
import no.trank.openpipe.parse.api.ParserResultImpl;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Parses .xls files. Reads numeric, string and boolean type cells only.
 * 
 * @version $Revision$
 */
public class ExcelParser implements Parser {
   public ParserResult parse(ParseData data) throws IOException, ParserException {
      POIFSFileSystem fs = new POIFSFileSystem(data.getInputStream());
      
      final HSSFWorkbook doc = new HSSFWorkbook(fs);
      Map<String, String> properties = POIUtils.getProperties(fs);
      
      final ParserResultImpl result = new ParserResultImpl();
      result.setText(getText(doc));
      result.setTitle(properties.get("title"));
      if(data.includeProperties()) {
         result.setProperties(properties);
      }

      return result;
   }
   
   private String getText(final HSSFWorkbook doc) {
      StringBuilder text = new StringBuilder();

      for (int sheetNumber = 0; sheetNumber < doc.getNumberOfSheets(); ++sheetNumber) {
         if (sheetNumber > 0) {
            text.append('\n');
         }

         HSSFSheet sheet = doc.getSheetAt(sheetNumber);

         for (Iterator rowIterator = sheet.rowIterator(); rowIterator.hasNext(); ) {
            HSSFRow row = (HSSFRow) rowIterator.next();

            boolean firstCell = true;
            for (Iterator cellIterator = row.cellIterator(); cellIterator.hasNext(); ) {
               HSSFCell cell = (HSSFCell) cellIterator.next();
               String cellText = getCellText(cell);
               if (cellText != null) {
                  if (!firstCell) {
                     text.append(' ');
                  }
                  firstCell = false;

                  text.append(cellText);
               }
            }
            if (!firstCell) {
               text.append('\n');
            }
         }
      }

      return text.toString();
   }

   private String getCellText(final HSSFCell cell) {
      String ret = null;

      // skip formula/error cells
      switch (cell.getCellType()) {
         case HSSFCell.CELL_TYPE_NUMERIC:
            ret = cell.getNumericCellValue() + "";
            break;
         case HSSFCell.CELL_TYPE_STRING:
            ret = cell.getRichStringCellValue() + "";
            break;
         case HSSFCell.CELL_TYPE_BOOLEAN:
            ret = cell.getBooleanCellValue() ? "true" : "false";
      }
      
      if(ret != null) {
         ret = ret.trim();
      }
      return ret != null && ret.length() > 0 ? ret : null;
   }
}