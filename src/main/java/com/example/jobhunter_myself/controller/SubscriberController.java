package com.example.jobhunter_myself.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobhunter_myself.domain.Subscriber;
import com.example.jobhunter_myself.repository.SubscriberRepository;
import com.example.jobhunter_myself.service.SubscriberService;
import com.example.jobhunter_myself.util.SecurityUtil;
import com.example.jobhunter_myself.util.annotation.ApiMessage;
import com.example.jobhunter_myself.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subcriberService;

    public SubscriberController(SubscriberService subcriberService) {
        this.subcriberService = subcriberService;
    }

    @PostMapping("/subscribers")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber subscriber)
            throws IdInvalidException {
        boolean isSubscriberEmailExist = subcriberService.isSubscriberExist(subscriber);
        if (isSubscriberEmailExist) {
            throw new IdInvalidException("Subscriber with email " + subscriber.getEmail() + " already exists");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(subcriberService.createSubscriber(subscriber));
    }

    @PutMapping("/subscribers")
    public ResponseEntity<Subscriber> updateSubscriber(@Valid @RequestBody Subscriber subscriber)
            throws IdInvalidException {
        if (this.subcriberService.fetchById(subscriber.getId()) == null) {
            throw new IdInvalidException("Subscriber with id " + subscriber.getId() + " does not exist");
        }
        return ResponseEntity.status(HttpStatus.OK).body(subcriberService.updateSubscriber(subscriber));
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skill")
    public ResponseEntity<Subscriber> getSubscribersSkill() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        return ResponseEntity.ok().body(this.subcriberService.findByEmail(email));
    }
}
