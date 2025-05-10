package org.example.finostra.Services.User.CreditCard;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.sas.*;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import jakarta.annotation.PostConstruct;
import org.example.finostra.Entity.Contract.Contract;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.CreditCard.AttachCreditRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.CreditCard.CarForCreditRequest;
import org.example.finostra.Entity.User.CreditCard.CreditCard;
import org.example.finostra.Entity.User.User;
import org.example.finostra.Repositories.User.ContractRepository;
import org.example.finostra.Repositories.User.CreditCard.CreditCardRepository;
import org.example.finostra.Services.User.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class CreditCardService {

    private final UserService          userService;
    private final CreditCardRepository cardRepo;
    private final ContractRepository contractRepo;

    @Value("${azure.storage.connection-string}")   private String connectionString;
    @Value("${azure.storage.container.contracts}") private String containerName;
    private BlobContainerClient container;

    public CreditCardService(UserService userService, CreditCardRepository cardRepo, ContractRepository contractRepo) {
        this.userService = userService;
        this.cardRepo = cardRepo;
        this.contractRepo = contractRepo;
    }

    @PostConstruct
    void init() {
        BlobServiceClient service =
                new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
        container = service.getBlobContainerClient(containerName);
        if (!container.exists()) container.create();
    }


    @Transactional
    public Contract attachCredit(String userUUID, AttachCreditRequest req) {

        User user = userService.getById(userUUID);
        CreditCard card = cardRepo.findByCardNumberAndUser(req.getCardNumber(), userUUID)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        card.set(card.getLoan().add(req.getCreditAmount()));
        cardRepo.save(card);

        byte[] pdf = buildCreditPdf(user, card, req);
        Contract contract = uploadPdfAndCreateContract(user, "credit-", pdf);
        return contractRepo.save(contract);
    }


    @Transactional
    public Contract createCarCreditRequest(String userUUID, CarForCreditRequest req) {

        User user = userService.getById(userUUID);

        byte[] pdf = buildCarCreditPdf(user, req);
        Contract contract = uploadPdfAndCreateContract(user, "car-credit-", pdf);
        return contractRepo.save(contract);
    }


    private byte[] buildCreditPdf(User u, CreditCard c, AttachCreditRequest r) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            var body = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            var bold = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);

            PdfDocument pdf = new PdfDocument(new PdfWriter(out));
            Document doc   = new Document(pdf, PageSize.A4);
            doc.setMargins(60, 60, 60, 60);

            doc.add(new Paragraph("Finostra")
                    .setFont(bold).setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("CREDIT AGREEMENT")
                    .setFont(bold).setFontSize(14).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("No. " + UUID.randomUUID())
                    .setFont(body).setFontSize(11).setTextAlignment(TextAlignment.CENTER));
            doc.add(new LineSeparator(new SolidLine()));

            Table t = new Table(UnitValue.createPercentArray(new float[]{38f, 62f}))
                    .useAllAvailableWidth().setBorder(Border.NO_BORDER)
                    .setFont(body).setFontSize(11);

            addRow(t, "Borrower:",     u.getUsername());
            addRow(t, "Card number:",  c.getCardNumber());
            addRow(t, "Amount:",       r.getCreditAmount() + " UAH");
            addRow(t, "Term:",         r.getMonths() + " months");
            addRow(t, "Interest rate:",r.getPercentage() + " %");
            doc.add(t);

            doc.add(signBlock("(Authorised signature)", body));

            doc.close();
            return out.toByteArray();

        } catch (Exception ex) {
            throw new IllegalStateException("PDF generation error", ex);
        }
    }

    private byte[] buildCarCreditPdf(User u, CarForCreditRequest r) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            var body = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            var bold = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);

            PdfDocument pdf = new PdfDocument(new PdfWriter(out));
            Document doc   = new Document(pdf, PageSize.A4);
            doc.setMargins(60, 60, 60, 60);

            doc.add(new Paragraph("Finostra")
                    .setFont(bold).setFontSize(16).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("CAR LOAN APPLICATION")
                    .setFont(bold).setFontSize(14).setTextAlignment(TextAlignment.CENTER));
            doc.add(new Paragraph("No. " + UUID.randomUUID())
                    .setFont(body).setFontSize(11).setTextAlignment(TextAlignment.CENTER));
            doc.add(new LineSeparator(new SolidLine()));

            Table t = new Table(UnitValue.createPercentArray(new float[]{40f, 60f}))
                    .useAllAvailableWidth().setBorder(Border.NO_BORDER)
                    .setFont(body).setFontSize(11);

            addRow(t, "Applicant:",        u.getUsername());
            addRow(t, "Car price:",        r.getCarPrice() + " UAH");
            addRow(t, "Down payment:",     r.getUserRate() + " UAH");
            addRow(t, "Car type:",         String.valueOf(r.getCarType()));
            addRow(t, "Loan term:",        r.getYears() + " years");
            addRow(t, "Monthly payment:",  r.getMonthlyPayment() + " UAH");
            addRow(t, "Interest rate:",    r.getCreditPercentage() + " %");
            doc.add(t);

            doc.add(signBlock("(Applicant signature)", body));

            doc.close();
            return out.toByteArray();

        } catch (Exception ex) {
            throw new IllegalStateException("PDF generation error", ex);
        }
    }


    private void addRow(Table t, String key, String value) {
        String safe = value == null ? "" : String.valueOf(value);
        t.addCell(new Cell().add(new Paragraph(key)));
        t.addCell(new Cell().add(new Paragraph(safe)));
    }

    private Table signBlock(String caption, PdfFont font) {
        Table sign = new Table(new float[]{50f, 50f})
                .useAllAvailableWidth().setBorder(Border.NO_BORDER)
                .setFont(font).setFontSize(11);

        sign.addCell(new Cell().add(new Paragraph("_______________________________\n" + caption)
                .setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));
        sign.addCell(new Cell().add(new Paragraph("\"___\" __________ 20__")
                .setTextAlignment(TextAlignment.CENTER)).setBorder(Border.NO_BORDER));
        return sign;
    }


    private Contract uploadPdfAndCreateContract(User u, String prefix, byte[] pdf) {

        String blobName = prefix + UUID.randomUUID() + ".pdf";
        BlobClient blob = container.getBlobClient(blobName);

        blob.upload(new ByteArrayInputStream(pdf), pdf.length, true);
        blob.setHttpHeaders(new BlobHttpHeaders().setContentType("application/pdf"));

        OffsetDateTime expiry = OffsetDateTime.now().plusMinutes(15);
        String sasToken = blob.generateSas(new BlobServiceSasSignatureValues(
                expiry, new BlobSasPermission().setReadPermission(true)));

        Contract contract = new Contract();
        contract.setUser(u);
        contract.setBlobLink(blobName);
        contract.setBlobLink(blob.getBlobUrl() + "?" + sasToken);
        contract.setCreatedAt(OffsetDateTime.now());

        return contract;
    }
}
