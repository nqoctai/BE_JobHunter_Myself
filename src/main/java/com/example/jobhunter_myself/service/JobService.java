package com.example.jobhunter_myself.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.jobhunter_myself.domain.Company;
import com.example.jobhunter_myself.domain.Job;
import com.example.jobhunter_myself.domain.Skill;
import com.example.jobhunter_myself.domain.response.ResultPaginationDTO;
import com.example.jobhunter_myself.domain.response.job.ResCreateJobDTO;
import com.example.jobhunter_myself.domain.response.job.ResUpdateJobDTO;
import com.example.jobhunter_myself.repository.CompanyRepository;
import com.example.jobhunter_myself.repository.JobRepository;
import com.example.jobhunter_myself.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;

    private final SkillRepository skillRepository;

    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public Optional<Job> findById(Long id) {
        return jobRepository.findById(id);
    }

    public ResCreateJobDTO createJob(Job rqJob) {

        // check skill
        if (rqJob.getSkills() != null) {
            List<Long> skillIds = rqJob.getSkills().stream().map(skill -> skill.getId()).collect(Collectors.toList());

            List<Skill> skills = skillRepository.findByIdIn(skillIds);
            rqJob.setSkills(skills);
        }

        // check company
        if (rqJob.getCompany() != null) {
            Optional<Company> company = companyRepository.findById(rqJob.getCompany().getId());
            if (company.isPresent()) {
                rqJob.setCompany(company.get());
            }
        }
        Job currentJob = jobRepository.save(rqJob);
        ResCreateJobDTO res = new ResCreateJobDTO();
        res.setId(currentJob.getId());
        res.setName(currentJob.getName());
        res.setSalary(currentJob.getSalary());
        res.setQuantity(currentJob.getQuantity());
        res.setLocation(currentJob.getLocation());
        res.setLevel(currentJob.getLevel());
        res.setStartDate(currentJob.getStartDate());
        res.setEndDate(currentJob.getEndDate());
        res.setActive(currentJob.isActive());
        res.setCreatedAt(currentJob.getCreatedAt());
        res.setCreatedBy(currentJob.getCreatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            res.setSkills(skills);
        }

        return res;
    }

    public ResUpdateJobDTO update(Job rqJob, Job jobInDB) {

        // check skill
        if (rqJob.getSkills() != null) {
            List<Long> skillIds = rqJob.getSkills().stream().map(skill -> skill.getId()).collect(Collectors.toList());

            List<Skill> skills = skillRepository.findByIdIn(skillIds);
            jobInDB.setSkills(skills);
        }

        // check company
        if (rqJob.getCompany() != null) {
            Optional<Company> company = companyRepository.findById(rqJob.getCompany().getId());
            if (company.isPresent()) {
                jobInDB.setCompany(company.get());
            }
        }

        jobInDB.setName(rqJob.getName());
        jobInDB.setSalary(rqJob.getSalary());
        jobInDB.setQuantity(rqJob.getQuantity());
        jobInDB.setLocation(rqJob.getLocation());
        jobInDB.setLevel(rqJob.getLevel());
        jobInDB.setStartDate(rqJob.getStartDate());
        jobInDB.setEndDate(rqJob.getEndDate());
        jobInDB.setActive(rqJob.isActive());

        Job currentJob = jobRepository.save(jobInDB);
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(currentJob.getId());
        dto.setName(currentJob.getName());
        dto.setSalary(currentJob.getSalary());
        dto.setQuantity(currentJob.getQuantity());
        dto.setLocation(currentJob.getLocation());
        dto.setLevel(currentJob.getLevel());
        dto.setStartDate(currentJob.getStartDate());
        dto.setEndDate(currentJob.getEndDate());
        dto.setActive(currentJob.isActive());
        dto.setUpdatedAt(currentJob.getUpdatedAt());
        dto.setUpdatedBy(currentJob.getUpdatedBy());

        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(item -> item.getName())
                    .collect(Collectors.toList());
            dto.setSkills(skills);
        }

        return dto;
    }

    public void delete(Long id) {
        jobRepository.deleteById(id);
    }

    public ResultPaginationDTO getAllJobs(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageJob = jobRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageJob.getTotalPages());
        meta.setTotal(pageJob.getTotalElements());
        rs.setMeta(meta);
        rs.setResult(pageJob.getContent());
        return rs;
    }
}
