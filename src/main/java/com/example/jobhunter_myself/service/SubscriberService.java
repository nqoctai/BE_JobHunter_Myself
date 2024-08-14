package com.example.jobhunter_myself.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.jobhunter_myself.domain.Job;
import com.example.jobhunter_myself.domain.Skill;
import com.example.jobhunter_myself.domain.Subscriber;
import com.example.jobhunter_myself.domain.response.email.ResEmailJob;
import com.example.jobhunter_myself.repository.JobRepository;
import com.example.jobhunter_myself.repository.SkillRepository;
import com.example.jobhunter_myself.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;

    private final SkillRepository skillRepository;

    private final JobRepository jobRepository;

    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository,
            JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public boolean isSubscriberExist(Subscriber subscriber) {
        return subscriberRepository.existsByEmail(subscriber.getEmail());
    }

    public Subscriber fetchById(long id) {
        return subscriberRepository.findById(id).orElse(null);
    }

    public Subscriber createSubscriber(Subscriber subscriber) {
        if (subscriber.getSkills() != null) {
            List<Long> skillID = subscriber.getSkills().stream().map(Skill::getId).collect(Collectors.toList());

            List<Skill> skills = this.skillRepository.findByIdIn(skillID);

            subscriber.setSkills(skills);
        }
        return subscriberRepository.save(subscriber);
    }

    public Subscriber updateSubscriber(Subscriber subscriber) {
        Subscriber subscriberInDB = this.fetchById(subscriber.getId());
        List<Long> skillID = subscriber.getSkills().stream().map(Skill::getId).collect(Collectors.toList());

        List<Skill> skills = this.skillRepository.findByIdIn(skillID);

        subscriberInDB.setSkills(skills);
        return subscriberRepository.save(subscriberInDB);
    }

    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());
        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> s = skills.stream().map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                .collect(Collectors.toList());
        res.setSkills(s);
        return res;
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {

                        List<ResEmailJob> arr = listJobs.stream().map(
                                job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());

                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }
    }

    public Subscriber findByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }

}
