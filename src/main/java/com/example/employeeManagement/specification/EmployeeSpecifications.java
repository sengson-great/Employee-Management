package com.example.employeeManagement.specification;

import com.example.employeeManagement.Model.Employee;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class EmployeeSpecifications {
    public static Specification<Employee> getEmployees(String search, String dept, String status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Search Filter (Name or Email)
            if (search != null && !search.trim().isEmpty()) {
                String pattern = "%" + search.toLowerCase() + "%";

                Predicate nameMatch = cb.like(cb.lower(root.get("fullName")), pattern);
                Predicate emailMatch = cb.like(cb.lower(root.join("user").get("email")), pattern);

                // This is like putting (Name OR Email) in brackets
                predicates.add(cb.or(nameMatch, emailMatch));
            }

            // Department Filter
            if (dept != null && !dept.isEmpty()) {
                predicates.add(cb.equal(root.get("department"), dept));
            }

            // Status Filter
            if (status != null && !status.isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
