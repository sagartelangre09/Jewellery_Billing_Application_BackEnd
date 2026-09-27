package com.jewellery.billing.service;

import com.ibm.icu.text.Transliterator;
import com.jewellery.billing.model.Bill;
import com.jewellery.billing.model.BillItem;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Service
public class BillPdfService {

    public byte[] generatePdf(Bill bill) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 20, 20, 20, 20);
            PdfWriter.getInstance(document, outputStream);

            document.open();

            // Fonts & Colors matching original bill layout
            Color headerBg = new Color(74, 20, 60); 
            Font topTaglineFont = new Font(Font.HELVETICA, 8, Font.NORMAL, Color.WHITE);
            Font shopNameFont = new Font(Font.HELVETICA, 20, Font.BOLD, Color.WHITE);
            Font shopSubFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);

            Font labelBold = new Font(Font.HELVETICA, 8, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 8, Font.NORMAL);
            Font tableHeaderFont = new Font(Font.HELVETICA, 8, Font.BOLD);
            Font sectionHeaderFont = new Font(Font.HELVETICA, 10, Font.BOLD);

            // ==========================================
            // 1. SHOP HEADER BROWSER BANNER
            // ==========================================
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(100);

            PdfPCell bannerCell = new PdfPCell();
            bannerCell.setBackgroundColor(headerBg);
            bannerCell.setPadding(8);

            Paragraph taglines = new Paragraph("|| Shri ||                                            Trust is our tradition...", topTaglineFont);
            taglines.setAlignment(Element.ALIGN_CENTER);
            bannerCell.addElement(taglines);
        
            Paragraph shopTitle = new Paragraph(bill.getShop().getEnglishShopName(), shopNameFont);
            shopTitle.setAlignment(Element.ALIGN_CENTER);
            bannerCell.addElement(shopTitle);

            headerTable.addCell(bannerCell);

            PdfPCell addressCell = new PdfPCell();
            addressCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            addressCell.setPadding(4);
            Paragraph addressPara = new Paragraph(
                safe(bill.getShop().getAddress()) + "  Mo. " + safe(bill.getShop().getPhone()),
                shopSubFont
            );
            addressPara.setAlignment(Element.ALIGN_CENTER);
            addressCell.addElement(addressPara);
            headerTable.addCell(addressCell);

            document.add(headerTable);

            // ==========================================
            // 2. CUSTOMER & BILL META INFO
            // ==========================================
            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{18, 42, 15, 25});

            addMetaRow(infoTable, "Name :", bill.getCustomerName(), "Bill No :", bill.getBillNumber(), labelBold, normalFont);
            addMetaRow(infoTable, "Address :", safe(bill.getCustomerAddress()), "URD No :", safe(bill.getUrdNumber()), labelBold, normalFont);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dateStr = bill.getCreatedAt() != null ? bill.getCreatedAt().format(formatter) : "";

            addMetaRow(infoTable, "Mob :", safe(bill.getCustomerPhone()), "Date :", dateStr, labelBold, normalFont);
            addMetaRow(infoTable, "PAN No. :", safe(bill.getCustomerPan()), "", "", labelBold, normalFont);

            document.add(infoTable);

            // ==========================================
            // 3. SECTION HEADER ("GOLD SALE")
            // ==========================================
            PdfPTable sectionTable = new PdfPTable(1);
            sectionTable.setWidthPercentage(100);
            PdfPCell sectionCell = new PdfPCell(new Phrase("GOLD SALE", sectionHeaderFont));
            sectionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            sectionCell.setPadding(3);
            sectionTable.addCell(sectionCell);

            document.add(sectionTable);

            // ==========================================
            // 4. ITEMS TABLE (Matching Form Input Fields)
            // ==========================================
            PdfPTable itemTable = new PdfPTable(7);
            itemTable.setWidthPercentage(100);
            itemTable.setWidths(new float[]{28, 12, 12, 12, 12, 12, 14});

            String[] headers = {
                "Description & HSN", "Metal", "Purity",
                "Gross Wt\nGm", "Rate\nPer Gm",
                "Making", "Amount"
            };

            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, tableHeaderFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setPadding(4);
                itemTable.addCell(cell);
            }

            BigDecimal totalSaleWeight = BigDecimal.ZERO;

            for (BillItem item : bill.getItems()) {
                String desc = item.getItemName() + (item.getHsn() != null && !item.getHsn().isEmpty() ? "\n" + item.getHsn() : "");
                addItemCell(itemTable, desc, normalFont, Element.ALIGN_LEFT);
                addItemCell(itemTable, safe(item.getMetal()), normalFont, Element.ALIGN_CENTER);
                addItemCell(itemTable, format(item.getGrossWeight()), normalFont, Element.ALIGN_RIGHT);
                addItemCell(itemTable, safe(item.getPurity()), normalFont, Element.ALIGN_CENTER);
                addItemCell(itemTable, money(item.getRatePerGram()), normalFont, Element.ALIGN_RIGHT);
                addItemCell(itemTable, money(item.getMakingCharges()), normalFont, Element.ALIGN_RIGHT);
                addItemCell(itemTable, money(item.getAmount()), labelBold, Element.ALIGN_RIGHT);

                if (item.getGrossWeight() != null) {
                    totalSaleWeight = totalSaleWeight.add(item.getGrossWeight());
                }
            }

            // Fill empty rows to retain standard print height
            for (int i = bill.getItems().size(); i < 4; i++) {
                for (int j = 0; j < 7; j++) {
                    PdfPCell emptyCell = new PdfPCell(new Phrase(" ", normalFont));
                    emptyCell.setPadding(8);
                    itemTable.addCell(emptyCell);
                }
            }

            document.add(itemTable);

            // ==========================================
            // 5. PAYMENT & TOTALS BREAKDOWN
            // ==========================================
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(100);
            summaryTable.setWidths(new float[]{60, 40});

            // Left Side: Cash Paid & Amount in Words
            PdfPTable leftSummary = new PdfPTable(2);
            leftSummary.setWidthPercentage(100);
            leftSummary.setWidths(new float[]{30, 70});

            addSummaryCell(leftSummary, "By Cash", labelBold, Element.ALIGN_LEFT);
            addSummaryCell(leftSummary, money(bill.getNetPayable()), normalFont, Element.ALIGN_LEFT);

//            PdfPCell wordsCell = new PdfPCell(new Phrase("Rs.: " + safe(bill.getAmountInWords()), normalFont));
//            wordsCell.setColspan(2);
//            wordsCell.setPadding(4);
//            leftSummary.addCell(wordsCell);

            PdfPCell leftCell = new PdfPCell(leftSummary);
            leftCell.setPadding(0);

            // Right Side: Subtotal, Discount, Less URD, Net Payable
            PdfPTable rightSummary = new PdfPTable(2);
            rightSummary.setWidthPercentage(100);
            rightSummary.setWidths(new float[]{50, 50});

            addSummaryCell(rightSummary, "Amount", labelBold, Element.ALIGN_LEFT);
            addSummaryCell(rightSummary, money(bill.getSubtotal()), normalFont, Element.ALIGN_RIGHT);

            addSummaryCell(rightSummary, "Discount", labelBold, Element.ALIGN_LEFT);
            addSummaryCell(rightSummary, money(bill.getDiscount()), normalFont, Element.ALIGN_RIGHT);

            addSummaryCell(rightSummary, "Tax/GST", labelBold, Element.ALIGN_LEFT);
            addSummaryCell(rightSummary, money(bill.getTax()), normalFont, Element.ALIGN_RIGHT);

            addSummaryCell(rightSummary, "Net Payable", labelBold, Element.ALIGN_LEFT);
            addSummaryCell(rightSummary, money(bill.getNetPayable()), labelBold, Element.ALIGN_RIGHT);

            PdfPCell rightCell = new PdfPCell(rightSummary);
            rightCell.setPadding(0);

            summaryTable.addCell(leftCell);
            summaryTable.addCell(rightCell);

            document.add(summaryTable);

            // Weight Totals
            PdfPTable weightSummary = new PdfPTable(4);
            weightSummary.setWidthPercentage(100);
            weightSummary.setWidths(new float[]{20, 30, 25, 25});

            BigDecimal saleWt = bill.getTotalSaleWeight() != null ? bill.getTotalSaleWeight() : totalSaleWeight;
            addSummaryCell(weightSummary, "Gold Sale Wt Total :", labelBold, Element.ALIGN_LEFT);
            addSummaryCell(weightSummary, format(saleWt), normalFont, Element.ALIGN_LEFT);

            addSummaryCell(weightSummary, "Gold URD Wt :", labelBold, Element.ALIGN_LEFT);
            addSummaryCell(weightSummary, format(bill.getTotalUrdWeight()), normalFont, Element.ALIGN_LEFT);

            document.add(weightSummary);

            // ==========================================
            // 6. DECLARATION & SIGNATURE
            // ==========================================
            PdfPTable footerTable = new PdfPTable(2);
            footerTable.setWidthPercentage(100);
            footerTable.setWidths(new float[]{65, 35});

            PdfPCell declCell = new PdfPCell();
            declCell.setPadding(4);
            declCell.addElement(new Paragraph(
                "Salesman : " + safe(bill.getSalesmanName()) + "            Billed By : " + safe(bill.getBilledBy()),
                labelBold
            ));
           // declCell.addElement(new Paragraph("IRN No. : " + safe(bill.getIrnNumber()), normalFont));
            declCell.addElement(new Paragraph(
                "\nWe Declare that this invoice shows the actual price of the goods described and all particulars are true and correct.",
                normalFont
            ));

            PdfPCell signCell = new PdfPCell();
            signCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            signCell.setPadding(4);

            Paragraph thanks = new Paragraph("\n\nThanks", labelBold);
            thanks.setAlignment(Element.ALIGN_CENTER);
            signCell.addElement(thanks);

            Paragraph signText = new Paragraph(bill.getShop().getShopName() + "Visit Again", labelBold);
            signText.setAlignment(Element.ALIGN_CENTER);
            signCell.addElement(signText);

            footerTable.addCell(declCell);
            footerTable.addCell(signCell);

            document.add(footerTable);

            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }

    private void addMetaRow(PdfPTable table, String lbl1, String val1, String lbl2, String val2, Font bold, Font norm) {
        PdfPCell c1 = new PdfPCell(new Phrase(lbl1, bold));
        c1.setBorder(Rectangle.NO_BORDER);
        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase(safe(val1), norm));
        c2.setBorder(Rectangle.NO_BORDER);
        table.addCell(c2);

        PdfPCell c3 = new PdfPCell(new Phrase(lbl2, bold));
        c3.setBorder(Rectangle.NO_BORDER);
        table.addCell(c3);

        PdfPCell c4 = new PdfPCell(new Phrase(safe(val2), norm));
        c4.setBorder(Rectangle.NO_BORDER);
        table.addCell(c4);
    }

    private void addItemCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(safe(text), font));
        cell.setHorizontalAlignment(align);
        cell.setPadding(4);
        table.addCell(cell);
    }

    private void addSummaryCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(safe(text), font));
        cell.setHorizontalAlignment(align);
        cell.setPadding(3);
        table.addCell(cell);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String format(BigDecimal value) {
        return value == null ? "0.000" : String.format("%.3f", value.doubleValue());
    }

    private String money(BigDecimal value) {
        return value == null ? "0.00" : String.format("%.2f", value.doubleValue());
    }
   
    
}