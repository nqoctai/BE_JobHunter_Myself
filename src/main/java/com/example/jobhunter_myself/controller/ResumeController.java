package com.example.jobhunter_myself.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.jobhunter_myself.domain.Company;
import com.example.jobhunter_myself.domain.Job;
import com.example.jobhunter_myself.domain.Resume;
import com.example.jobhunter_myself.domain.User;
import com.example.jobhunter_myself.domain.response.ResultPaginationDTO;
import com.example.jobhunter_myself.domain.response.resume.ResCreateResumeDTO;
import com.example.jobhunter_myself.domain.response.resume.ResFetchResumeDTO;
import com.example.jobhunter_myself.domain.response.resume.ResUpdateResumeDTO;
import com.example.jobhunter_myself.service.ResumeService;
import com.example.jobhunter_myself.service.UserService;
import com.example.jobhunter_myself.util.SecurityUtil;
import com.example.jobhunter_myself.util.annotation.ApiMessage;
import com.example.jobhunter_myself.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;
    private final UserService userService;
    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeController(ResumeService resumeService, UserService userService, FilterBuilder filterBuilder,
            FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create Resume")
    public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume rqResume)
            throws IdInvalidException {
        boolean isExitsUserAndJob = resumeService.checkResumeExistByUserAndJob(rqResume);
        if (!isExitsUserAndJob) {
            throw new IdInvalidException("User or Job not found");
        }
        ResCreateResumeDTO resume = resumeService.createResume(rqResume);
        return ResponseEntity.status(HttpStatus.CREATED).body(resume);
    }

    @PutMapping("/resumes")
    @ApiMessage("Update Resume")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume rqResume)
            throws IdInvalidException {
        Optional<Resume> resumeOptional = resumeService.fetchById(rqResume.getId());
        if (!resumeOptional.isPresent()) {
            throw new IdInvalidException("Resume not found");
        }
        Resume resume = resumeOptional.get();
        resume.setStatus(rqResume.getStatus());
        return ResponseEntity.status(HttpStatus.OK).body(this.resumeService.updateResume(resume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete Resume")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> resumeOptional = resumeService.fetchById(id);
        if (!resumeOptional.isPresent()) {
            throw new IdInvalidException("Resume not found");
        }
        resumeService.deleteResume(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get Resume By Id")
    public ResponseEntity<ResFetchResumeDTO> getResumeById(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> resumeOptional = resumeService.fetchById(id);
        if (!resumeOptional.isPresent()) {
            throw new IdInvalidException("Resume not found");
        }
        return ResponseEntity.ok().body(this.resumeService.getResume(resumeOptional.get()));
    }

    @GetMapping("/resumes")
    @ApiMessage("Fetch all resume with paginate")
    public ResponseEntity<ResultPaginationDTO> fetchAll(
            @Filter Specification<Resume> spec,
            Pageable pageable) {

        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User currentUser = this.userService.getUserByEmail(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    arrJobIds = companyJobs.stream().map(x -> x.getId())
                            .collect(Collectors.toList());
                }
            }
        }

        Specification<Resume> jobInSpec = filterSpecificationConverter.convert(filterBuilder.field("job")
                .in(filterBuilder.input(arrJobIds)).get());

        Specification<Resume> finalSpec = jobInSpec.and(spec);

        return ResponseEntity.ok().body(this.resumeService.getAllResume(finalSpec, pageable));
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {

        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }
}
