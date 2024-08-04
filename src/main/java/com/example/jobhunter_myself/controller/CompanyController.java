package com.example.jobhunter_myself.controller;

import java.util.List;

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
import com.example.jobhunter_myself.domain.response.ResultPaginationDTO;
import com.example.jobhunter_myself.service.CompanyService;
import com.example.jobhunter_myself.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company rqCompany) throws IdInvalidException {
        boolean isExist = companyService.isExistCompany(rqCompany.getName());
        if (isExist) {
            throw new IdInvalidException("Tên công ty đã tồn tại");
        }
        Company company = companyService.createCompany(rqCompany);
        return ResponseEntity.status(HttpStatus.CREATED).body(company);
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company rqCompany) throws IdInvalidException {
        Company company = companyService.updateCompany(rqCompany);
        return ResponseEntity.ok(company);
    }

    @GetMapping("/companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(@Filter Specification<Company> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.companyService.getAllCompanies(spec, pageable));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) throws IdInvalidException {
        if (this.companyService.fetchCompanyById(id) == null) {
            throw new IdInvalidException("Không có công ty chứa id này");
        }
        this.companyService.deleteCompany(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable("id") long id) throws IdInvalidException {
        Company company = this.companyService.fetchCompanyById(id);
        if (company == null) {
            throw new IdInvalidException("Không có công ty chứa id này");
        }
        return ResponseEntity.ok(company);
    }
}
