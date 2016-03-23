package com.github.keraton.easydsl.repository;

import com.github.keraton.easydsl.dto.DSLBeanMethod;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DSLRepo {

    private List<DSLBeanMethod> DSLBeanMethods = new ArrayList<>();

    public void addAll(List<DSLBeanMethod> DSLBeanMethods) {
        this.DSLBeanMethods.addAll(DSLBeanMethods);
    }

    public List<DSLBeanMethod> getAll() {
        return this.DSLBeanMethods;
    }
}
