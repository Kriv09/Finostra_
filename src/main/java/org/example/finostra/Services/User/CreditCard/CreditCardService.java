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
import org.example.finostra.Entity.RequestsAndDTOs.Requests.BankCard.CreateBankCardRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.CreditCard.AttachCreditRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.CreditCard.CarForCreditRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.CreditCard.CreateCreditCardRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Responses.CreditCard.GetAllCreditCardResponse;
import org.example.finostra.Entity.User.CreditCard.CreditCard;
import org.example.finostra.Entity.User.CreditCard.CurrencyType;
import org.example.finostra.Entity.User.User;
import org.example.finostra.Entity.User.UserProfile.UserProfile;
import org.example.finostra.Repositories.User.ContractRepository;
import org.example.finostra.Repositories.User.CreditCard.CreditCardRepository;
import org.example.finostra.Services.User.UserService;
import org.example.finostra.Utils.BankCards.BankCardUtils;
import org.example.finostra.Utils.BankCards.CardType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CreditCardService {

    private final UserService userService;
    private final CreditCardRepository cardRepo;
    private final ContractRepository contractRepo;
    private final CreditCardRepository creditCardRepository;

    @Value("${azure.storage.connection-string}")
    private String connectionString;
    @Value("${azure.storage.container.contracts}")
    private String containerName;
    private BlobContainerClient container;

    public CreditCardService(UserService userService, CreditCardRepository cardRepo, ContractRepository contractRepo, CreditCardRepository creditCardRepository) {
        this.userService = userService;
        this.cardRepo = cardRepo;
        this.contractRepo = contractRepo;
        this.creditCardRepository = creditCardRepository;
    }

    @PostConstruct
    void init() {
        BlobServiceClient service =
                new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
        container = service.getBlobContainerClient(containerName);
        if (!container.exists()) container.create();
    }

    @Transactional
    public CreditCard createCreditCard(String userUUID, CreateCreditCardRequest request) {
        User user = userService.getById(userUUID);

        String cardNumber = BankCardUtils.generateCardNumber(request.getCardType());
        LocalDate expirationDate = BankCardUtils.generateExpirationDate(5);
        String IBAN = BankCardUtils.generateIBAN(user.getId());
        Boolean active = true;
        CurrencyType currency = request.getCurrency();

        CreditCard creditCard = CreditCard.builder()
                .cardNumber(cardNumber)
                .expiryDate(expirationDate)
                .IBAN(IBAN)
                .active(active)
                .user(user)
                .build();


        creditCardRepository.save(creditCard);

        return creditCard;
    }

    @Transactional
    public List<GetAllCreditCardResponse.CreditCardInfo> getCreditCards(String userUUID) {
        User user = userService.getById(userUUID);

        var allCard = creditCardRepository.findAllByUser(user);

        var crediCards = allCard.stream().map(
                card -> new GetAllCreditCardResponse.CreditCardInfo(
                        card.getCardNumber(),
                        card.getIBAN(),
                        card.getExpiryDate(),
                        card.getBalance().getLoan()
                )).collect(Collectors.toList());

        return crediCards;
    }

    @Transactional
    public Contract attachCredit(String userUUID, AttachCreditRequest req) {

        User user = userService.getById(userUUID);

        CreditCard creditCard = createCreditCard(userUUID, CreateCreditCardRequest.builder()
                .cardType(req.getCardType())
                .currency(req.getCurrencyType())
                .build());

        creditCard.set(creditCard.getLoan().add(req.getCreditAmount()));
        cardRepo.save(creditCard);

        byte[] pdf = buildCreditPdf(user, creditCard, req);
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
            Document doc = new Document(pdf, PageSize.A4);
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

            addRow(t, "Borrower:", getBorrower(u));
            addRow(t, "Card number:", c.getCardNumber());
            addRow(t, "Amount:", r.getCreditAmount() + " UAH");
            addRow(t, "Term:", r.getMonths() + " months");
            addRow(t, "Interest rate:", r.getPercentage() + " %");
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
            Document doc = new Document(pdf, PageSize.A4);
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

            addRow(t, "Applicant:", getBorrower(u));
            addRow(t, "Car price:", r.getCarPrice() + " UAH");
            addRow(t, "Down payment:", r.getUserRate() + " UAH");
            addRow(t, "Car type:", String.valueOf(r.getCarType()));
            addRow(t, "Loan term:", r.getYears() + " years");
            addRow(t, "Monthly payment:", r.getMonthlyPayment() + " UAH");
            addRow(t, "Interest rate:", r.getCreditPercentage() + " %");
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

    private String getBorrower(User u) {
        String borrower = u.getUsername();
        UserProfile profile = u.getUserProfile();
        if (profile != null) {
            String firstName = profile.getFirstNameEn().substring(0, 1).toUpperCase() + profile.getFirstNameEn().substring(1).toLowerCase();
            String lastName = profile.getLastNameEn().substring(0, 1).toUpperCase() + profile.getLastNameEn().substring(1).toLowerCase();
            String patronymic = profile.getPatronymicEn().substring(0, 1).toUpperCase() + profile.getPatronymicEn().substring(1).toLowerCase();
            borrower = String.format("%s %s %s", firstName, lastName, patronymic);
        }
        return borrower;
    }
}
