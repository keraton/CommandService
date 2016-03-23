package com.github.keraton.easydsl;

import java.util.Collections;
import java.util.Map;

public class DSLContext {

    public final Map<String, String> header;

    public DSLContext(Map<String, String> header) {
        this.header = Collections.unmodifiableMap(header);
    }

    public Map<String, String> getHeader() {
        return header;
    }
}
