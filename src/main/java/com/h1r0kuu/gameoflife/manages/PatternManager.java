package com.h1r0kuu.gameoflife.manages;

import com.h1r0kuu.gameoflife.models.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatternManager {
    private static final Logger logger = LogManager.getLogger(PatternManager.class);

    private final List<Pattern> patterns = new ArrayList<>();

    public PatternManager() {
        logger.info("PatternManager init");
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public void addPattern(Pattern pattern) {
        patterns.add(pattern);
    }

    public Pattern getByName(String name) {
        Optional<Pattern> pattern = patterns.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst();
        if(pattern.isPresent()) {
            return pattern.get();
        }
        logger.info("Pattern not found");
        return null;
    }
}
