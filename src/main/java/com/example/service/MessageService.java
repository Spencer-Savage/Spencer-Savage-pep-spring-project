package com.example.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Message;
import com.example.exception.InvalidMessageException;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

@Service
public class MessageService {

    private MessageRepository msgDao;
    private AccountRepository accDao;

    @Autowired
    public MessageService(MessageRepository peramMessageRepository, AccountRepository peramAccountRepository){
        this.msgDao = peramMessageRepository;
        this.accDao = peramAccountRepository;
    }

    public Optional<Message> insertMessage(Message peramMessage) throws InvalidMessageException{
        if (validate(peramMessage, false)){
            return Optional.of(msgDao.save(peramMessage));
        }
        throw new InvalidMessageException();
    }

    public Optional<List<Message>> getAllMessages(){ 
        return Optional.of(msgDao.findAll());
    }

    public Optional<Message> getMessage(Integer messageId){
        //Check for null since Integer is an object.
         if(messageId == null){
             return Optional.empty();
        }
        return msgDao.getMessageByMessageId(messageId);
     }
    

    public boolean deleteMessage(Integer messageId){
       //Check for null since Integer is an object.
        if(messageId == null){
            return false;
       }
       if(msgDao.getMessageByMessageId(messageId).isPresent()){
            msgDao.deleteById(messageId);
            return true;
       }
       return false;
    }

    public boolean updateMessage(Message peramMessage){
        if(!validate(peramMessage, true)){
            return false;
        }
        Optional<Message> updateMessage = msgDao.getMessageByMessageId(peramMessage.getMessageId());
        if(updateMessage.isPresent()){
            updateMessage.get().setMessageText(peramMessage.getMessageText());
            msgDao.save(updateMessage.get());
            return true;
        }
        return false;
    }

    public Optional<List<Message>> getMessagesByAccount(Integer accountId){
        if(accountId == null){
            return Optional.of(List.of());
        }
        return msgDao.findByPostedBy(accountId);
    }

    private boolean validate(Message messageToValidate, boolean isUpdate){
        if(messageToValidate == null || messageToValidate.getMessageText().isBlank()){
            return false;
        }
        if(messageToValidate.getMessageText().length() > 255){
            return false;
        }
        if(!isUpdate && accDao.getAccountByAccountId(messageToValidate.getPostedBy()).isEmpty()){
            return false;
        } 
        return true;
    }
}
