package org.obiba.magma.datasource.excel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.obiba.magma.Datasource;
import org.obiba.magma.MagmaEngine;
import org.obiba.magma.ValueTableWriter;
import org.obiba.magma.Variable;
import org.obiba.magma.ValueTableWriter.VariableWriter;
import org.obiba.magma.type.TextType;

import com.google.common.collect.Iterables;

public class ExcelDatasourceTest {

  @Before
  public void before() {
    new MagmaEngine();
  }

  @After
  public void after() {
    MagmaEngine.get().shutdown();
  }

  @Test
  public void testWriteVariableIsReadBack() throws IOException {
    File tmpExcelFile = createTempFile(".xlsx");

    Variable testVariable = Variable.Builder.newVariable("test-variable", TextType.get(), "entityType").build();

    ExcelDatasource datasource = new ExcelDatasource("test", tmpExcelFile);
    datasource.initialise();
    writeVariableToDatasource(datasource, "test-table", testVariable);
    datasource.dispose();

    datasource = new ExcelDatasource("test", tmpExcelFile);
    datasource.initialise();
    Assert.assertNotNull(datasource.getValueTable("test-table"));
    Assert.assertNotNull(datasource.getValueTable("test-table").getVariable("test-variable"));
  }

  // Test for OPAL-232
  @Test
  public void testWriteVariableMultipleTimes() throws IOException {
    File tmpExcelFile = createTempFile(".xlsx");

    Variable testVariable = Variable.Builder.newVariable("test-variable", TextType.get(), "entityType").build();

    ExcelDatasource datasource = new ExcelDatasource("test", tmpExcelFile);
    datasource.initialise();
    writeVariableToDatasource(datasource, "test-table", testVariable);
    datasource.dispose();

    datasource = new ExcelDatasource("test", tmpExcelFile);
    datasource.initialise();
    writeVariableToDatasource(datasource, "test-table", testVariable);
    datasource.dispose();

    datasource = new ExcelDatasource("test", tmpExcelFile);
    datasource.initialise();
    Assert.assertEquals(1, Iterables.size(datasource.getValueTable("test-table").getVariables()));
  }

  @Test
  public void testOPAL_238() throws IOException {
    File tmp = createTempFile(".xlsx");

    Workbook w = new XSSFWorkbook();
    Sheet s = w.createSheet();
    int i = 0;
    for(String str : readStrings("src/test/resources/org/obiba/magma/datasource/excel/opal-238-strings.txt")) {
      s.createRow(i++).createCell(0).setCellValue(str);
    }

    Throwable t = null;
    try {
      w.write(new FileOutputStream(tmp));
      w = new XSSFWorkbook(new FileInputStream(tmp));
      Assert.fail("If this test fails, it may mean that OPAL-238 is fixed. If so, reverse the assertions of this test.");
    } catch(RuntimeException e) {
      t = e;
    } catch(AssertionError e) {
      t = e;
    }
    Assert.assertNotNull("Due to a bug in POI, we expect to get an exception or an AssertionError (depends whether assertions are enabled).", t);
  }

  @Test
  public void testCreateDatasourceOnEmptyExcelFile() {
    ExcelDatasource datasource = new ExcelDatasource("empty", new File("src/test/resources/org/obiba/magma/datasource/excel/empty.xls"));
    datasource.initialise();
  }

  private List<String> readStrings(String filename) throws IOException {
    List<String> strs = new ArrayList<String>();
    BufferedReader bis = new BufferedReader(new FileReader(new File(filename)));
    String s;
    while((s = bis.readLine()) != null) {
      if(s.trim().length() > 0) strs.add(s.trim());
    }
    return strs;
  }

  private File createTempFile(String suffix) throws IOException {
    File tmpFile = File.createTempFile("test", suffix);
    tmpFile.delete();
    // tmpFile.deleteOnExit();
    return tmpFile;
  }

  private void writeVariableToDatasource(Datasource datasource, String tableName, Variable testVariable) throws IOException {
    ValueTableWriter writer = datasource.createWriter("test-table", "entityType");
    VariableWriter vw = writer.writeVariables();
    vw.writeVariable(testVariable);
    vw.close();
    writer.close();
  }
}
