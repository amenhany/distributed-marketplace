package org.team13.marketplace.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.team13.marketplace.dto.auth.AccountInfoResponse;
import org.team13.marketplace.dto.auth.LoginRequest;
import org.team13.marketplace.dto.auth.RegisterRequest;
import org.team13.marketplace.dto.item.ItemDto;
import org.team13.marketplace.dto.user.ProfileResponse;
import org.team13.marketplace.dto.user.UserDto;
import org.team13.marketplace.exception.MarketplaceException;
import org.team13.marketplace.mapper.ItemMapper;
import org.team13.marketplace.mapper.UserMapper;
import org.team13.marketplace.model.User;
import org.team13.marketplace.model.UserAccount;
import org.team13.marketplace.repository.ItemRepository;
import org.team13.marketplace.repository.TransactionRepository;
import org.team13.marketplace.repository.UserAccountRepository;
import org.team13.marketplace.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAccountRepository userAccountRepository;
    private final ItemRepository itemRepository;
    private final TransactionRepository transactionRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UserDto register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername()))
            throw new MarketplaceException("Username already taken");
        if (userRepository.existsByUsername(req.getUsername()))
            throw new MarketplaceException("Email already registered");

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        userAccountRepository.save(UserAccount.builder().id(user.getId()).build());

        return UserMapper.toDto(user);
    }

    public UserDto login(LoginRequest req) {
        User user = userRepository
                .findByUsername(req.getUsername())
                .orElseThrow(() -> new MarketplaceException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
            throw new MarketplaceException("Invalid credentials");

        return UserMapper.toDto(user);
    }

    private User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new MarketplaceException("User not found"));
    }

    private UserAccount getAccountById(String id) {
        return userAccountRepository.findById(id)
                .orElseThrow(() -> new MarketplaceException("Account not found"));
    }

    public double deposit(String userId, double amount) {
        if (amount <= 0)
            throw new MarketplaceException("Deposit amount must be positive");

        UserAccount account = getAccountById(userId);
        account.setBalance(account.getBalance() + amount);
        userAccountRepository.save(account);
        return account.getBalance();
    }

    public UserDto getUser(String userId) {
        return UserMapper.toDto(getUserById(userId));
    }

    public AccountInfoResponse getAccountInfo(String userId) {
        User user = getUserById(userId);
        UserAccount account = getAccountById(userId);
        UserActivity activity = fetchUserActivity(userId);

        return AccountInfoResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .balance(account.getBalance())
                .ownedItems(activity.ownedItems())
                .soldItems(activity.soldItems())
                .purchasedItems(activity.boughtItems())
                .build();
    }

    public ProfileResponse getProfile(String userId) {
        User user = getUserById(userId);
        UserActivity activity = fetchUserActivity(userId);

        return ProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .ownedItems(activity.ownedItems())
                .soldItems(activity.soldItems())
                .purchasedItems(activity.boughtItems())
                .build();
    }

    private record UserActivity(List<ItemDto> ownedItems, List<ItemDto> boughtItems, List<ItemDto> soldItems) {
    }

    private UserActivity fetchUserActivity(String userId) {
        List<ItemDto> owned = itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::toDto).toList();

        List<ItemDto> bought = transactionRepository.findByBuyerId(userId).stream()
                .map(t -> ItemMapper.toDto(t.getPurchasedItem())).toList();

        List<ItemDto> sold = transactionRepository.findBySellerId(userId).stream()
                .map(t -> ItemMapper.toDto(t.getPurchasedItem())).toList();

        return new UserActivity(owned, bought, sold);
    }
}
