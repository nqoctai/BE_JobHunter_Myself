package com.example.jobhunter_myself.controller;

import java.util.Optional;

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

import com.example.jobhunter_myself.domain.Resume;
import com.example.jobhunter_myself.domain.response.ResultPaginationDTO;
import com.example.jobhunter_myself.domain.response.resume.ResCreateResumeDTO;
import com.example.jobhunter_myself.domain.response.resume.ResFetchResumeDTO;
import com.example.jobhunter_myself.domain.response.resume.ResUpdateResumeDTO;
import com.example.jobhunter_myself.service.ResumeService;
import com.example.jobhunter_myself.util.annotation.ApiMessage;
import com.example.jobhunter_myself.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
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
    @ApiMessage("Get All Resume")
    public ResponseEntity<ResultPaginationDTO> getAllResume(@Filter Specification<Resume> spec, Pageable pageable) {
        return ResponseEntity.ok().body(resumeService.getAllResume(spec, pageable));
    }
}
