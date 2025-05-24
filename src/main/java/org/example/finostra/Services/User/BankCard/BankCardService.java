package org.example.finostra.Services.User.BankCard;

import jakarta.transaction.Transactional;
import org.example.finostra.Entity.RequestsAndDTOs.DTO.BankCard.BankCardDTO;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.BankCard.CreateBankCardRequest;
import org.example.finostra.Entity.RequestsAndDTOs.Responses.GetBankCardResponse;
import org.example.finostra.Entity.User.BankCards.Balance;
import org.example.finostra.Entity.User.BankCards.BankCard;
import org.example.finostra.Entity.User.BankCards.CVVCode;
import org.example.finostra.Entity.User.BankCards.CurrencyType;
import org.example.finostra.Entity.User.User;
import org.example.finostra.Exceptions.UserCardNotFoundException;
import org.example.finostra.Services.User.UserService;
import org.example.finostra.Utils.Mappers.BankCard.BankCardMapper;
import org.example.finostra.Repositories.User.BankCard.BankCardRepository;
import org.example.finostra.Repositories.User.UserRepository;
import org.example.finostra.Utils.BankCards.BankCardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BankCardService {

    private final BankCardRepository bankCardRepository;
    private final UserRepository userRepository;
    private final BankCardMapper bankCardMapper;
    private final BalanceService balanceService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserService userService;


    @Value("${TMP_OBJECTS_SECURITYCODE}")
    private String CVV_KEY;

    @Value("${CVV_LIFETIME_MINUTES}")
    private long CVV_LIFETIME_MINUTES;

    @Autowired
    public BankCardService(BankCardRepository bankCardRepository, UserRepository userRepository,
                           RedisTemplate<String, Object> redisTemplate, BankCardMapper bankCardMapper,
                           BalanceService balanceService, UserService userService) {
        this.bankCardRepository = bankCardRepository;
        this.userRepository = userRepository;
        this.bankCardMapper = bankCardMapper;
        this.balanceService = balanceService;
        this.redisTemplate = redisTemplate;
        this.userService = userService;
    }

    public List<BankCardDTO> fetchAllBankCards() {
        return bankCardRepository.findAll().stream()
                .map(bankCardMapper::toDTO)
                .collect(Collectors.toList());
    }



    public BankCardDTO fetchBankCardById(Long bankCardId) {
        Optional<BankCard> bankCard = bankCardRepository.findById(bankCardId);
        if (bankCard.isEmpty()) {
            throw new UserCardNotFoundException("BankCard not found");
        }

        return bankCardMapper.toDTO(bankCard.get());
    }

    public Optional<BankCard> fetchRawByPublicUUID(String bankCardPublicUUID) {
        return bankCardRepository.findByPublicUUID(bankCardPublicUUID);
    }

    public BankCardDTO fetchBankCardByCardNumber(String cardNumber) {
        Optional<BankCard> bankCard = bankCardRepository.findByCardNumber(cardNumber);
        if (bankCard.isEmpty()) {
            throw new UserCardNotFoundException("BankCard not found");
        }
        return bankCardMapper.toDTO(bankCard.get());
    }

    public BankCardDTO fetchBankCardByIBAN(String IBAN) {
        Optional<BankCard> bankCard = bankCardRepository.findByIBAN(IBAN);
        if (bankCard.isEmpty()) {
            throw new UserCardNotFoundException("BankCard not found");
        }
        return bankCardMapper.toDTO(bankCard.get());
    }

    public List<BankCardDTO> fetchBankCardsByUserId(Long userId) {
        List<BankCard> bankCards = bankCardRepository.findByUserId(userId);

        return bankCards.stream()
                .map(bankCardMapper::toDTO)
                .collect(Collectors.toList());
    }



    @Transactional
    public void createBankCard(CreateBankCardRequest createBankCardRequest , String publicUUID) {
        User user = userRepository.getByPublicUUID(publicUUID);


        String cardNumber = BankCardUtils.generateCardNumber(createBankCardRequest.getCardType());
        LocalDate expirationDate = BankCardUtils.generateExpirationDate(5);
        String IBAN = BankCardUtils.generateIBAN(user.getId());
        Boolean active = true;
        CurrencyType currency = createBankCardRequest.getCurrency();

        do {
            if (bankCardRepository.existsByCardNumber(cardNumber)) {
                cardNumber = BankCardUtils.generateCardNumber(createBankCardRequest.getCardType());
            }

            if (bankCardRepository.existsByIBAN(IBAN)) {
                IBAN = BankCardUtils.generateIBAN(user.getId());
            }

        } while (bankCardRepository.existsByCardNumber(cardNumber)
                || bankCardRepository.existsByIBAN(IBAN));

        BankCard bankCard = BankCard.builder()
                .cardNumber(cardNumber)
                .expiryDate(expirationDate)
                .IBAN(IBAN)
                .user(user)
                .active(active)
                .build();

        bankCardRepository.save(bankCard);
        balanceService.createBalanceForBankCard(bankCard.getId(), currency);
    }

    public void blockBankCard(Long id) {
        Optional<BankCard> bankCard = bankCardRepository.findById(id);
        if (bankCard.isEmpty()) {
            throw new UserCardNotFoundException("BankCard not found");
        }
        bankCard.get().setActive(false);
        bankCardRepository.save(bankCard.get());
    }

    public void unblockBankCard(Long id) {
        Optional<BankCard> bankCard = bankCardRepository.findById(id);
        if (bankCard.isEmpty()) {
            throw new UserCardNotFoundException("BankCard not found");
        }
        bankCard.get().setActive(true);
        bankCardRepository.save(bankCard.get());
    }

    public CVVCode fetchOrGenerateCVV(Long bankCardId) {
        if (bankCardRepository.findById(bankCardId).isEmpty()) {
            throw new UserCardNotFoundException("BankCard not found");
        }

        String redisKey = String.format("%s:%s", CVV_KEY, bankCardId.toString());
        CVVCode cvvCode = (CVVCode) redisTemplate.opsForValue().get(redisKey);

        if (cvvCode != null) {
            Long newExpTimeInSec = redisTemplate.getExpire(redisKey);
            if (newExpTimeInSec != null && newExpTimeInSec > 0) {
                cvvCode.setExpiryTimeInMinutes(newExpTimeInSec / 60);
            }
            return cvvCode;
        }

        cvvCode = CVVCode.builder()
                .bankCardId(bankCardId)
                .cvv(BankCardUtils.generateCVV())
                .expiryTimeInMinutes(CVV_LIFETIME_MINUTES)
                .build();

        redisTemplate.opsForValue().set(redisKey, cvvCode);
        redisTemplate.expire(redisKey, CVV_LIFETIME_MINUTES, TimeUnit.MINUTES);

        return cvvCode;
    }


    @Transactional
    public List<GetBankCardResponse.CardInfo> fetchBankCardsByUserId(String userPublicUUID, CurrencyType currency) {
        User user = userService.getById(userPublicUUID);
        List<BankCard> allCard =
                bankCardRepository.findAllByUserPublicUUIDAndCurrency(user.getPublicUUID(), currency);
        List<GetBankCardResponse.CardInfo> cardInfoList = allCard.stream()
                .map(card -> new GetBankCardResponse.CardInfo(
                        card.getCardNumber(),
                        this.fetchOrGenerateCVV(card.getId()).getCvv(),
                        card.getExpiryDate(),
                        balanceService.fetchBalanceByBankCardId(card.getId())
                )).collect(Collectors.toList());
        return cardInfoList;
    }


    @Transactional
    public Optional<BankCard> fetchRawByCardNumber(String cardNumber)
    {
        return bankCardRepository.findByCardNumber(cardNumber);
    }

    @Transactional
    public ResponseEntity<GetBankCardResponse> fetchAllBankCardsByUserPublicUUID(String userPublicUUID) {

        List<BankCard> cards = bankCardRepository.findAllByUserPublicUUID(userPublicUUID);

        if (cards.isEmpty()) {
            return ResponseEntity.ok(GetBankCardResponse.EMPTY);
        }

        List<GetBankCardResponse.CardInfo> dtoList = cards.stream()
                .map(card -> new GetBankCardResponse.CardInfo(
                        card.getCardNumber(),
                        this.fetchOrGenerateCVV(card.getId()).getCvv(),
                        card.getExpiryDate(),
                        balanceService.fetchBalanceByBankCardId(card.getId())
                ))
                .toList();

        return ResponseEntity.ok(new GetBankCardResponse(dtoList));
    }
}
