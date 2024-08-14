package com.example.jobhunter_myself.controller;

import java.lang.StackWalker.Option;
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

import com.example.jobhunter_myself.domain.Permission;
import com.example.jobhunter_myself.domain.response.ResultPaginationDTO;
import com.example.jobhunter_myself.service.PermissionService;
import com.example.jobhunter_myself.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission)
            throws IdInvalidException {
        boolean isPermissionNameExist = permissionService.isPermissionExist(permission);
        if (isPermissionNameExist) {
            throw new IdInvalidException("Permission already exists");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(permissionService.createPermission(permission));
    }

    @PutMapping("/permissions")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission)
            throws IdInvalidException {
        Optional<Permission> permissionOptional = permissionService.fetchPermissionById(permission.getId());
        if (permissionOptional.isEmpty()) {
            throw new IdInvalidException("Permission not found");
        }

        // boolean isPermissionNameExist =
        // permissionService.isPermissionExist(permission);
        // if (isPermissionNameExist) {
        // throw new IdInvalidException("Permission already exists");
        // }

        if (this.permissionService.isPermissionExist(permission)) {
            // check name
            if (this.permissionService.isSameName(permission))
                throw new IdInvalidException("Permission đã tồn tại.");
        }
        return ResponseEntity.ok(permissionService.updatePermission(permission));
    }

    @GetMapping("/permissions")
    public ResponseEntity<ResultPaginationDTO> getPermission(@Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity.ok(permissionService.getPermission(spec, pageable));
    }

    @DeleteMapping("/permissions/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Permission> permissionOptional = permissionService.fetchPermissionById(id);
        if (permissionOptional.isEmpty()) {
            throw new IdInvalidException("Permission not found");
        }
        permissionService.deletePermission(id);
        return ResponseEntity.ok().body(null);
    }

}
