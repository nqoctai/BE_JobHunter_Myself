package com.example.jobhunter_myself.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.jobhunter_myself.domain.Skill;
import com.example.jobhunter_myself.domain.response.ResultPaginationDTO;
import com.example.jobhunter_myself.repository.SkillRepository;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill createSkill(Skill rqSkill) {
        return skillRepository.save(rqSkill);
    }

    public boolean isExistSkill(String name) {
        return skillRepository.existsByName(name);
    }

    public Skill getSkillById(Long id) {
        Optional<Skill> skillDB = skillRepository.findById(id);
        if (skillDB.isPresent()) {
            return skillDB.get();
        } else {
            return null;
        }
    }

    public Skill updateSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    public ResultPaginationDTO getAllSkill(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageSkill.getTotalPages());
        meta.setTotal(pageSkill.getTotalElements());
        rs.setMeta(meta);
        rs.setResult(pageSkill.getContent());
        return rs;
    }

    public void deleteSkill(long id) {
        Optional<Skill> skillOP = skillRepository.findById(id);
        Skill currentSkill = skillOP.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));
        skillRepository.delete(currentSkill);
    }
}
