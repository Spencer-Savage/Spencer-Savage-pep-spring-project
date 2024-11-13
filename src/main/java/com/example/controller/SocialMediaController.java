package com.example.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.BadRequestException;
import com.example.exception.DuplicateUsernameException;
import com.example.exception.InvalidMessageException;
import com.example.exception.InvalidRegistrationException;
import com.example.exception.UnauthorizedException;
import com.example.service.AccountService;
import com.example.service.MessageService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */

@RestController
@RequestMapping("/")
public class SocialMediaController {

    private AccountService accService;
    private MessageService msgService;

    @Autowired
    public SocialMediaController(AccountService peramAccountService, MessageService peramMessageService){
        this.accService = peramAccountService;
        this.msgService = peramMessageService;
    }
    
    @PostMapping("register")
    public ResponseEntity registerUser(@RequestBody Account newAccount) throws InvalidRegistrationException, DuplicateUsernameException {
        Optional<Account> registeredAccount = accService.insertAccount(newAccount);
        if(registeredAccount.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(registeredAccount.get());
        }
        throw new InvalidRegistrationException();
    }

    @PostMapping("login")
    public ResponseEntity loginUser(@RequestBody Account potentialUser) throws UnauthorizedException {
        Optional<Account> loggedInAccount = accService.loginAccount(potentialUser);
        if(loggedInAccount.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(loggedInAccount.get());
        }
        throw new UnauthorizedException();
    }

    @PostMapping("messages")
    public ResponseEntity postMessage(@RequestBody Message newPost) throws InvalidMessageException {
        Optional<Message> postedMessage = msgService.insertMessage(newPost);
        if(postedMessage.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(postedMessage.get());
        }
        throw new InvalidMessageException();
    }

    @GetMapping("messages")
    public ResponseEntity getMessages() throws InvalidMessageException {
        Optional<List<Message>> messages = msgService.getAllMessages();
        if(messages.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(messages.get());
        }
        return ResponseEntity.status(HttpStatus.OK).body(List.of());
    }

    @GetMapping("messages/{message_id}")
    public ResponseEntity getMessage(@PathVariable String message_id){
        try {
            Optional<Message> retrievedMessage = msgService.getMessage(Integer.parseInt(message_id));
            if(retrievedMessage.isPresent()){
                return ResponseEntity.status(HttpStatus.OK).body(retrievedMessage.get());
            };
            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body("");
        }
    }

    @DeleteMapping("messages/{message_id}")
    public ResponseEntity deleleteMessage(@PathVariable String message_id){
        try {
            Integer parsedMessageId = Integer.parseInt(message_id);
            if(msgService.deleteMessage(parsedMessageId)){
                return ResponseEntity.status(HttpStatus.OK).body("1");
            };
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body("");
        }
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PatchMapping("messages/{message_id}")
    public ResponseEntity patchMessage(@PathVariable String message_id, @RequestBody Message updatePost) throws BadRequestException{
        try {
            updatePost.setMessageId(Integer.parseInt(message_id));
            if(msgService.updateMessage(updatePost)){
                return ResponseEntity.status(HttpStatus.OK).body("1");
            };
            throw new BadRequestException();
        } catch (Exception e) {
            throw new BadRequestException();
        }
    }

    @GetMapping("accounts/{account_id}/messages")
    public ResponseEntity getAccountMessages(@PathVariable String account_id) throws BadRequestException{
        try {
            Optional<List<Message>> accountMessages = msgService.getMessagesByAccount(Integer.parseInt(account_id));
            if(accountMessages.isPresent()){
                return ResponseEntity.status(HttpStatus.OK).body(accountMessages.get());
            };
            return ResponseEntity.status(HttpStatus.OK).body("");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.OK).body("");
        }
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handlesUnauthorizedException(UnauthorizedException e){
        return "";
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handlesBadRequestException(BadRequestException e){
        return "";
    }

    @ExceptionHandler(InvalidRegistrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handlesInvalidRegistrationException(InvalidRegistrationException e){
        return "Ivalid Registration: Incorrect account information given.";
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handlesDuplicateUsernameException(DuplicateUsernameException e){
        return "Ivalid Registration: Duplicate username given.";
    }

    @ExceptionHandler(InvalidMessageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handlesInvalidMessageException(InvalidMessageException e){
        return "Ivalid Message: Incorrect message information given.";
    }
}
