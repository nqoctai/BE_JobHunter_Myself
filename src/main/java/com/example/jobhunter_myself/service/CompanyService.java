package com.example.jobhunter_myself.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.jobhunter_myself.domain.Company;
import com.example.jobhunter_myself.domain.User;
import com.example.jobhunter_myself.domain.response.ResultPaginationDTO;
import com.example.jobhunter_myself.repository.CompanyRepository;
import com.example.jobhunter_myself.repository.UserRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company createCompany(Company rqCompany) {
        return companyRepository.save(rqCompany);
    }

    public boolean isExistCompany(String name) {
        return companyRepository.existsByName(name);
    }

    public Company fetchCompanyById(long id) {
        Optional<Company> companyOP = companyRepository.findById(id);
        if (companyOP.isPresent()) {
            return companyOP.get();
        }
        return null;
    }

    public Optional<Company> findById(long id) {
        return companyRepository.findById(id);
    }

    public Company updateCompany(Company rqCompany) {
        Company companyDB = this.fetchCompanyById(rqCompany.getId());
        if (companyDB != null) {
            companyDB.setName(rqCompany.getName());
            companyDB.setAddress(rqCompany.getAddress());
            companyDB.setDescription(rqCompany.getDescription());
            companyDB.setLogo(rqCompany.getLogo());
            companyDB = companyRepository.save(companyDB);
        }
        return companyDB;

    }

    public ResultPaginationDTO getAllCompanies(Specification<Company> filter, Pageable pageable) {
        Page<Company> companies = this.companyRepository.findAll(filter, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(companies.getTotalPages());
        meta.setTotal(companies.getTotalElements());
        rs.setMeta(meta);
        rs.setResult(companies.getContent());
        return rs;
    }

    public void deleteCompany(long id) {
        Company companyDB = this.fetchCompanyById(id);
        if (companyDB != null) {
            List<User> users = companyDB.getUsers();
            this.userRepository.deleteAll(users);
        }
        companyRepository.deleteById(id);
    }

}
