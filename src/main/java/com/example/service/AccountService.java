package com.example.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.exception.DuplicateUsernameException;
import com.example.exception.InvalidRegistrationException;
import com.example.repository.AccountRepository;

@Service
public class AccountService {

    AccountRepository dao;

    @Autowired
    public AccountService(AccountRepository accountRepository){
        this.dao = accountRepository;
    }

    public Optional<Account> insertAccount( Account peramAccount) throws InvalidRegistrationException, DuplicateUsernameException{
        if (validateAccount(peramAccount)){
            return Optional.of(dao.save(peramAccount));
        }
        return Optional.empty();
    }

    public Optional<Account> loginAccount(Account peramAccount){
        return dao.getAccountByUsernameAndPassword(peramAccount.getUsername(), peramAccount.getPassword());
    }

    boolean validateAccount(Account potentialAcc) throws InvalidRegistrationException, DuplicateUsernameException{
        if (potentialAcc.getUsername().isBlank()){
            throw new InvalidRegistrationException();
        }
        if (potentialAcc.getUsername().length() < 4){
            throw new InvalidRegistrationException();
        }
        if (dao.getAccountByUsername(potentialAcc.getUsername()).isPresent()){
            throw new DuplicateUsernameException();
        }

        return true;
    }
}
