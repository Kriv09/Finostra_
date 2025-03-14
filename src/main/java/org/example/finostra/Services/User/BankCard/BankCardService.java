package org.example.finostra.Services.User.BankCard;

import org.example.finostra.Entity.RequestsAndDTOs.DTO.BankCard.BankCardDTO;
import org.example.finostra.Entity.RequestsAndDTOs.Requests.BankCard.CreateBankCardRequest;
import org.example.finostra.Entity.User.BankCards.BankCard;
import org.example.finostra.Entity.User.BankCards.CVVCode;
import org.example.finostra.Entity.User.BankCards.CurrencyType;
import org.example.finostra.Entity.User.User;
import org.example.finostra.Exceptions.UserCardNotFoundException;
import org.example.finostra.Exceptions.UserNotFoundException;
import org.example.finostra.Utils.Mappers.BankCard.BankCardMapper;
import org.example.finostra.Repositories.User.BankCard.BankCardRepository;
import org.example.finostra.Repositories.User.UserRepository;
import org.example.finostra.Utils.BankCards.BankCardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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


    @Value("${TMP_OBJECTS_SECURITYCODE}")
    private String CVV_KEY;

    @Value("${CVV_LIFETIME_MINUTES}")
    private long CVV_LIFETIME_MINUTES;

    @Autowired
    public BankCardService(BankCardRepository bankCardRepository, UserRepository userRepository,
                           RedisTemplate<String, Object> redisTemplate, BankCardMapper bankCardMapper,
                           BalanceService balanceService) {
        this.bankCardRepository = bankCardRepository;
        this.userRepository = userRepository;
        this.bankCardMapper = bankCardMapper;
        this.balanceService = balanceService;
        this.redisTemplate = redisTemplate;
    }

    // Temporary, it shouldn't return all cards due to privacy, I think
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

    public BankCardDTO fetchBankCardByCardNumber(String cardNumber) {
        Optional<BankCard> bankCard = bankCardRepository.findByCardNumber(cardNumber);
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


    public void createBankCard(CreateBankCardRequest createBankCardRequest) {
        Optional<User> user = userRepository.findById(createBankCardRequest.getUserId());
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found with id " + createBankCardRequest.getUserId());
        }

        String ownerName = createBankCardRequest.getOwnerName();
        String cardNumber = BankCardUtils.generateCardNumber();
        LocalDate expirationDate = BankCardUtils.generateExpirationDate(5);
        String IBAN = BankCardUtils.generateIBAN(user.get().getId());
        User userForBankCard = user.get();
        Boolean active = true;
        CurrencyType currency = createBankCardRequest.getCurrency();

        do {
            if (bankCardRepository.existsByCardNumber(cardNumber)) {
                cardNumber = BankCardUtils.generateCardNumber();
            }

            if (bankCardRepository.existsByIBAN(IBAN)) {
                IBAN = BankCardUtils.generateIBAN(user.get().getId());
            }

        } while (bankCardRepository.existsByCardNumber(cardNumber)
                || bankCardRepository.existsByIBAN(IBAN));

        BankCard bankCard = BankCard.builder()
                .ownerName(ownerName)
                .cardNumber(cardNumber)
                .expiryDate(expirationDate)
                .IBAN(IBAN)
                .user(userForBankCard)
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
}
