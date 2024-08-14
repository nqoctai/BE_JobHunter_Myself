package com.example.jobhunter_myself.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobhunter_myself.service.EmailService;
import com.example.jobhunter_myself.service.SubscriberService;
import com.example.jobhunter_myself.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final EmailService emailService;

    private final SubscriberService subscriberService;

    public EmailController(EmailService emailService, SubscriberService subscriberService) {
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    @ApiMessage("Send a simple email")
    // @Scheduled(cron = "*/30 * * * * *")
    // @Transactional
    public String sendSimpleEmail() {
        // this.emailService.sendSimpleEmail();
        // this.emailService.sendEmailSync("zerefmavis1210@gmail.com", "test send
        // email", "<h1> <b> Hello </b> </h1>",
        // false, true);

        this.subscriberService.sendSubscribersEmailJobs();
        return "ok";
    }
}
