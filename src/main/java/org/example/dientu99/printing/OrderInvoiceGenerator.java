package org.example.dientu99.printing;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.example.dientu99.dto.orderDTO.OrderSummaryDTO;
import org.example.dientu99.dto.productDTO.ProductDisplayInfoDTO;
import org.example.dientu99.utils.MoneyUtils;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderInvoiceGenerator {

    public static void exportToPdf(OrderSummaryDTO summary, List<ProductDisplayInfoDTO> productList) {
        try {
            Document document = new Document();
            String fileName = "phieu_ban_giao_" + summary.id() + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            BaseFont baseFont = BaseFont.createFont("c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 16, Font.BOLD);
            Font subTitleFont = new Font(baseFont, 11, Font.ITALIC);
            Font boldFont = new Font(baseFont, 11, Font.BOLD);
            Font regularFont = new Font(baseFont, 10);
            Font smallFont = new Font(baseFont, 9);
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 1});
            PdfPCell companyCell = new PdfPCell();
            companyCell.setBorder(Rectangle.NO_BORDER);
            Paragraph companyPara = new Paragraph();
            companyPara.add(new Phrase("CÔNG TY ĐIỆN TỬ 99\n", boldFont));
            companyPara.add(new Phrase("194 Đội cấn, Ba Đình, Hà Nội\n", smallFont));
            companyPara.add(new Phrase("ĐT: (0214) 779779 - ĐKKD: 5555273888 (Nguyễn Văn A) - NH TPBank\n", smallFont));
            companyPara.add(new Phrase("Email: dientu99@gmail.com - Website: dientu99.vn", smallFont));
            companyCell.addElement(companyPara);
            headerTable.addCell(companyCell);

            // Page number cell
            PdfPCell pageCell = new PdfPCell(new Phrase("1 / 1", regularFont));
            pageCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            pageCell.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(pageCell);

            document.add(headerTable);
            document.add(new Paragraph(" "));

            // Invoice title
            Paragraph titlePara = new Paragraph("PHIẾU MUA HÀNG", titleFont);
            titlePara.setAlignment(Element.ALIGN_CENTER);
            document.add(titlePara);

            Paragraph subTitlePara = new Paragraph("(Kiêm phiếu bảo hành)", subTitleFont);
            subTitlePara.setAlignment(Element.ALIGN_CENTER);
            document.add(subTitlePara);
            document.add(new Paragraph(" "));

            // Customer info section
            PdfPTable customerTable = new PdfPTable(2);
            customerTable.setWidthPercentage(100);
            customerTable.setWidths(new float[]{1, 1});

            // Customer info
            PdfPCell customerInfoCell = new PdfPCell();
            customerInfoCell.setBorder(Rectangle.NO_BORDER);
            Paragraph customerInfo = new Paragraph();
            customerInfo.add(new Phrase("Tên khách hàng: " + summary.customerName() + "\n", regularFont));
            customerInfo.add(new Phrase("Địa chỉ: " + "194 Đội Cấn  - Ba Đình" + "\n", regularFont));
            customerInfo.add(new Phrase("Điện thoại: " + "0934431316", regularFont));
            customerInfoCell.addElement(customerInfo);
            customerTable.addCell(customerInfoCell);

            // Order info
            PdfPCell orderInfoCell = new PdfPCell();
            orderInfoCell.setBorder(Rectangle.NO_BORDER);
            orderInfoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String currentDate = sdf.format(new Date());

            Paragraph orderInfo = new Paragraph();
            orderInfo.add(new Phrase("Ngày: " + currentDate + "\n", regularFont));
            orderInfo.add(new Phrase("Số: DH" + summary.id() + "\n", regularFont));
            orderInfo.add(new Phrase("Loại hóa đơn: ", regularFont));
            orderInfoCell.addElement(orderInfo);
            customerTable.addCell(orderInfoCell);

            document.add(customerTable);
            document.add(new Paragraph(" "));

            PdfPTable productsTable = new PdfPTable(5);
            productsTable.setWidthPercentage(100);
            productsTable.setWidths(new float[]{5, 1, 1, 1, 1});

            PdfPCell cell1 = new PdfPCell(new Phrase("Tên hàng hóa, dịch vụ", boldFont));
            PdfPCell cell2 = new PdfPCell(new Phrase("ĐVT", boldFont));
            PdfPCell cell3 = new PdfPCell(new Phrase("Số lượng", boldFont));
            PdfPCell cell4 = new PdfPCell(new Phrase("Đơn giá", boldFont));
            PdfPCell cell5 = new PdfPCell(new Phrase("Thành tiền", boldFont));

            productsTable.addCell(cell1);
            productsTable.addCell(cell2);
            productsTable.addCell(cell3);
            productsTable.addCell(cell4);
            productsTable.addCell(cell5);

            for (ProductDisplayInfoDTO product : productList) {
                String productWithWarranty = product.id() + " - " + product.name() + "\n";
                productsTable.addCell(new Phrase(productWithWarranty, regularFont));
                productsTable.addCell(new Phrase("Cái", regularFont));
                productsTable.addCell(new Phrase(product.quantity().toString(), regularFont));
                productsTable.addCell(new Phrase(MoneyUtils.formatVN(product.unitPrice()), regularFont));
                productsTable.addCell(new Phrase(MoneyUtils.formatVN(product.totalPrice()), regularFont));
            }

            document.add(productsTable);

            PdfPTable totalTable = new PdfPTable(5);
            totalTable.setWidthPercentage(100);
            totalTable.setWidths(new float[]{5, 1, 1, 1, 1});

            PdfPCell totalLabelCell = new PdfPCell(new Phrase("Tổng tiền thanh toán:", boldFont));
            totalLabelCell.setColspan(4);
            totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalTable.addCell(totalLabelCell);

            PdfPCell totalAmountCell = new PdfPCell(new Phrase(MoneyUtils.formatVN(summary.totalPrice().add(summary.shippingFee())), boldFont));
            totalTable.addCell(totalAmountCell);

            PdfPCell amountInWordsCell = new PdfPCell(new Phrase("Số tiền bằng chữ: " + MoneyUtils.convertToWords(summary.totalPrice().add(summary.shippingFee())) + " đồng chẵn", smallFont));
            amountInWordsCell.setColspan(5);
            totalTable.addCell(amountInWordsCell);

            document.add(totalTable);

            Paragraph warrantyTitle = new Paragraph("ĐIỀU KIỆN BẢO HÀNH:", boldFont);
            document.add(warrantyTitle);

            Paragraph warrantyTerms = new Paragraph();
            warrantyTerms.add(new Phrase("- Xin quý khách vui lòng kiểm tra kỹ trước khi nhận hàng!\n", smallFont));
            warrantyTerms.add(new Phrase("- Điều kiện bảo hành: Bảo hành theo đúng tiêu chuẩn nhà sản xuất \n", smallFont));
            warrantyTerms.add(new Phrase("- Để biết thêm thông tin chi tiết về chính sách BH vui lòng truy cập: www.dientu99.vn\n", smallFont));
            warrantyTerms.add(new Phrase("Những điều kiện sau không được bảo hành:\n", smallFont));
            warrantyTerms.add(new Phrase("- Hết thời hạn, tem gốc của nhà SX, công ty bị rách, bị sửa đổi, sai Serial hoặc ngày Bảo hành", smallFont));
            document.add(warrantyTerms);

            document.add(new Paragraph(" "));

            PdfPTable signatureTable = new PdfPTable(2);
            signatureTable.setWidthPercentage(100);

            PdfPCell staffCell = new PdfPCell(new Phrase("Nhân viên bàn giao\n(Ký, họ tên)", regularFont));
            staffCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            staffCell.setBorder(Rectangle.NO_BORDER);

            PdfPCell customerCell = new PdfPCell(new Phrase("Người mua hàng\n(Ký, họ tên)", regularFont));
            customerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            customerCell.setBorder(Rectangle.NO_BORDER);

            signatureTable.addCell(staffCell);
            signatureTable.addCell(customerCell);

            document.add(signatureTable);

            document.close();

            Desktop.getDesktop().open(new File(fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}