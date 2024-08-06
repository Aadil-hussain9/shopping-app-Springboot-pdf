package com.jhc.ProductService.service;

import com.jhc.ProductService.entity.Order;
import com.jhc.ProductService.entity.Product;
import com.jhc.ProductService.entity.User;
import com.jhc.ProductService.model.ProductResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class InvoicePDFService {

    @Autowired
    ProductService productService;

    @Value("classpath:background.png")
    private Resource watermarkImage;

    @Value("classpath:digitalSign.png")
    private Resource digitalSign;

    public void generateInvoice(User user, List<Order> orders, OutputStream outputStream) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document,page);

        // Company name
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
        contentStream.setNonStrokingColor(0, 128, 0); // Green color
        contentStream.newLineAtOffset(220, 750);
        contentStream.showText("isoTech ltd.");
        contentStream.endText();

        // Watermark image
        PDImageXObject pdImage = PDImageXObject.createFromFile(watermarkImage.getFile().getAbsolutePath(), document);
        contentStream.drawImage(pdImage, 90, 300, 400, 300);

        // User name and shipping address
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.setNonStrokingColor(0, 0, 0); // Black color
        contentStream.newLineAtOffset(50, 700);
        contentStream.showText("Name: " + user.getName());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Address: " + user.getAddresses().get(1).getStreet() + ", "
                + user.getAddresses().get(1).getCity() + ", " + user.getAddresses().get(1).getCountry());
        contentStream.newLineAtOffset(350, 0);
        contentStream.showText("Invoice number : #000" + user.getId());
        contentStream.endText();

        drawHorizontalLine(contentStream, 50, 680, page.getMediaBox().getWidth() - 100);

//        contentStream.beginText();
//        contentStream.setFont(PDType1Font.HELVETICA, 12);
//        contentStream.setNonStrokingColor(0, 0, 0); // Black color
//        contentStream.newLineAtOffset(350, 0);
//        contentStream.showText("Invoice number : #000" + user.getId());
//        contentStream.endText();

        // Table header
        float margin = 50;
        float yStart = 650;
        float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
        float yPosition = yStart;
        float tableHeight = 20;

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.setNonStrokingColor(0, 0, 0); // Black color
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Product Name");
        contentStream.newLineAtOffset(200, 0);
        contentStream.showText("Rate");
        contentStream.newLineAtOffset(100, 0);
        contentStream.showText("Quantity");
        contentStream.newLineAtOffset(100, 0);
        contentStream.showText("Amount");
        contentStream.endText();

        // Table rows
        yPosition -= tableHeight;
        float overallTotalAmount = 0;
        for (Order order : orders) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
            contentStream.newLineAtOffset(margin, yPosition);
            // Assuming you have a method to get product details from the order
            Product product = getProductDetails(order.getProductId());
            float rate = product.getPrice();
            long quantity = order.getQuantity();
            float total = rate * quantity;
            overallTotalAmount += total;

            contentStream.showText(product.getProductName());
            contentStream.newLineAtOffset(210, 0);
            contentStream.showText(String.valueOf(product.getPrice()));
            contentStream.newLineAtOffset(110, 0);
            contentStream.showText(String.valueOf(order.getQuantity()));
            contentStream.newLineAtOffset(100, 0);
            contentStream.showText(String.valueOf(total));

//            long totalAmount = order.getQuantity() * order.getAmount();
//            contentStream.showText(String.valueOf(totalAmount));
            contentStream.endText();
            yPosition -= tableHeight;
        }
        drawHorizontalLine(contentStream, margin, yPosition - 5, tableWidth);

        contentStream.beginText();
        contentStream.newLineAtOffset(margin + 350, yPosition - 30);
        contentStream.showText("Total Amount : ");
        contentStream.newLineAtOffset(60, 0);
        contentStream.showText(String.valueOf(overallTotalAmount));
//        contentStream.newLineAtOffset(margin+10, yPosition - 100);
        contentStream.newLineAtOffset(-60, -15);
        contentStream.showText("Paid Through : ");
        contentStream.showText(user.getTransactions().get(1).getPaymentMode());
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("Status : ");
        contentStream.showText(user.getTransactions().get(1).getPaymentStatus());
        if(!Objects.equals(user.getTransactions().get(1).getPaymentMode(), "CASH")) {
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Transaction Number : ");
            contentStream.showText(user.getTransactions().get(1).getReferenceNumber());
        }
        contentStream.endText();
//        long totalAmount = order.getQuantity() * order.getAmount();
//            contentStream.showText(String.valueOf(totalAmount));

        // Terms and condition
        contentStream.beginText();
        contentStream.setNonStrokingColor(0, 0, 255); //blue
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(margin, yPosition - 250);
        contentStream.showText("Terms and Conditions: ");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("1. Payment due within 30 days.");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("2. No returns after 30 days.");
        contentStream.endText();

        // Digital signature (assuming you have a digital signature image)
        PDImageXObject signatureImage = PDImageXObject.createFromFile(digitalSign.getFile().getAbsolutePath(), document);

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(margin + 350, yPosition - 300);
        contentStream.setStrokingColor(0, 0, 0); // Black color
        contentStream.showText("Authorised signature: ");
        contentStream.endText();
        contentStream.drawImage(signatureImage, margin + 350, yPosition - 350, 100, 50);


        contentStream.close();
        document.save(outputStream);
        document.close();
    }

    private Product getProductDetails(long productId) {
      ProductResponse product = productService.getProductById(productId);
      Product product1 = new Product();
      copyProperties(product, product1);
      return product1;
    }

    private void drawHorizontalLine(PDPageContentStream contentStream, float startX, float startY, float width) throws IOException {
        contentStream.setStrokingColor(0, 0, 0); // Black color
        contentStream.setLineWidth(1);
        contentStream.moveTo(startX, startY);
        contentStream.lineTo(startX + width, startY);
        contentStream.stroke();
    }
}

