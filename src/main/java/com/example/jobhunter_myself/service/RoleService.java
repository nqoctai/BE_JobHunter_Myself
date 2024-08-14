package com.example.jobhunter_myself.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.jobhunter_myself.domain.Permission;
import com.example.jobhunter_myself.domain.Role;
import com.example.jobhunter_myself.domain.response.ResultPaginationDTO;
import com.example.jobhunter_myself.repository.PermissionRepository;
import com.example.jobhunter_myself.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean isExistRole(String name) {
        return roleRepository.existsByName(name);
    }

    public Role fetchById(long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role createRole(Role role) {
        if (role.getPermissions() != null) {
            List<Long> permissionID = role.getPermissions().stream().map(Permission::getId)
                    .collect(Collectors.toList());
            List<Permission> permissions = this.permissionRepository.findByIdIn(permissionID);
            role.setPermissions(permissions);
        }
        return roleRepository.save(role);
    }

    public Role updateRole(Role role) {
        Role roleDB = this.fetchById(role.getId());
        if (role.getPermissions() != null) {
            List<Long> permissionID = role.getPermissions().stream().map(Permission::getId)
                    .collect(Collectors.toList());
            List<Permission> permissions = this.permissionRepository.findByIdIn(permissionID);
            roleDB.setPermissions(permissions);
        }
        roleDB.setName(role.getName());
        roleDB.setDescription(role.getDescription());
        roleDB.setActive(role.isActive());
        return roleRepository.save(roleDB);
    }

    public ResultPaginationDTO getRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = roleRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageRole.getTotalPages());
        meta.setTotal(pageRole.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(pageRole.getContent());

        return rs;
    }

    public void deleteRole(long id) {
        roleRepository.deleteById(id);
    }
}
