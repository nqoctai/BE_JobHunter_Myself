package com.example.jobhunter_myself.controller;

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

import com.example.jobhunter_myself.domain.Skill;
import com.example.jobhunter_myself.domain.response.ResultPaginationDTO;
import com.example.jobhunter_myself.service.SkillService;
import com.example.jobhunter_myself.util.annotation.ApiMessage;
import com.example.jobhunter_myself.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill rqSkill) throws IdInvalidException {
        boolean isExist = skillService.isExistSkill(rqSkill.getName());
        if (isExist) {
            throw new IdInvalidException("Tên kỹ năng đã tồn tại");
        }
        Skill skill = skillService.createSkill(rqSkill);
        return ResponseEntity.status(HttpStatus.CREATED).body(skill);
    }

    @PutMapping("/skills")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill rqSkill) throws IdInvalidException {
        Skill skillDB = skillService.getSkillById(rqSkill.getId());

        // check id
        if (skillDB == null) {
            throw new IdInvalidException("Id Skill này không tồn tại");
        }

        // check name
        if (this.skillService.isExistSkill(rqSkill.getName()) && rqSkill.getName() != null) {
            throw new IdInvalidException("Tên kỹ năng đã tồn tại");
        }

        skillDB.setName(rqSkill.getName());

        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.updateSkill(skillDB));
    }

    @GetMapping("/skills")
    public ResponseEntity<ResultPaginationDTO> getAll(@Filter Specification<Skill> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.getAllSkill(spec, pageable));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws IdInvalidException {
        Skill skill = skillService.getSkillById(id);
        if (skill == null) {
            throw new IdInvalidException("Id Skill này không tồn tại");
        }
        skillService.deleteSkill(id);
        return ResponseEntity.ok().body(null);

    }
}
