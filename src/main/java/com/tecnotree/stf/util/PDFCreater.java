package com.tecnotree.stf.util;

import java.io.FileOutputStream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFCreater {
	

	static String[] pdfHeader = {"Method Type","Response","Response Code","Status(PASS/FAIL)"};
	static private PdfPTable table = null;
	static Document document = null;
	static PdfWriter pdfWriter = null;
	
	public static void createPDF(String fileName, int columnNos, int[] colsWidth){
		document = new Document();
		try
		{
			pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(fileName));
			document.open();
			table = new PdfPTable(columnNos); // no of columns.
			table.setWidths(colsWidth); //Width 100%
			//table.setSpacingBefore(10f); //Space before table
			//table.setSpacingAfter(10f); //Space after table
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	
	public static void writeDataOnTable(String[] rowData){
		if(table == null){
			System.out.println("File writer is null");
			return;
		}
		for (String columnData : rowData) {
			table.addCell(columnData);
		}
	}
	
	public static void writeTableDataToFile(){
		if(document == null){
			System.out.println("Document is null");
			return;
		}
		try {
			document.add(table);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public static void closePdfFile(){
		if(document != null){	document.close();	}
		if(pdfWriter != null){	pdfWriter.close();	}
	}
	
	public static void main(String[] args) {
		//createPDF("D:/offeringJSON/system_test_report.pdf", 4);
		System.out.println("PDF generated..");
	}

}
