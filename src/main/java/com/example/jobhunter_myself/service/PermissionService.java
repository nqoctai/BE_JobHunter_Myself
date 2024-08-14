package com.example.jobhunter_myself.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.jobhunter_myself.domain.Permission;
import com.example.jobhunter_myself.domain.response.ResultPaginationDTO;
import com.example.jobhunter_myself.repository.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission permission) {
        return permissionRepository.existsByModuleAndApiPathAndMethod(permission.getModule(), permission.getApiPath(),
                permission.getMethod());
    }

    public Optional<Permission> fetchPermissionById(Long id) {
        return permissionRepository.findById(id);
    }

    public Permission createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    public Permission updatePermission(Permission permission) {
        Optional<Permission> permissionOptional = permissionRepository.findById(permission.getId());
        Permission permissionDB = permissionOptional.get();
        permissionDB.setName(permission.getName());
        permissionDB.setApiPath(permission.getApiPath());
        permissionDB.setMethod(permission.getMethod());
        permissionDB.setModule(permission.getModule());
        permissionDB = this.permissionRepository.save(permissionDB);
        return permissionDB;
    }

    public ResultPaginationDTO getPermission(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePermission = permissionRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pagePermission.getTotalPages());
        mt.setTotal(pagePermission.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pagePermission.getContent());

        return rs;

    }

    public void deletePermission(long id) {
        Optional<Permission> permissionOptional = permissionRepository.findById(id);
        Permission permissionDB = permissionOptional.get();
        permissionDB.getRoles().forEach(role -> role.getPermissions().remove(permissionDB));
        permissionRepository.delete(permissionDB);
    }

    public boolean isSameName(Permission p) {
        Permission permissionDB = this.fetchPermissionById(p.getId()).get();
        if (permissionDB != null) {
            if (permissionDB.getName().equals(p.getName())) {
                return true;
            }
        }
        return false;
    }
}
