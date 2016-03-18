package me.bbr.fun;

import java.util.Collections;
import java.util.Map;

public class CommandContext {

    public final Map<String, String> header;

    public CommandContext(Map<String, String> header) {
        this.header = Collections.unmodifiableMap(header);
    }

    public Map<String, String> getHeader() {
        return header;
    }
}
