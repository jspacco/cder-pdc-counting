package cder.pdc.counting;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Vote configuration. Reads values from application.yml (or eventually command line).
 * TODO: rename VoteConfiguration or VoteProperties
 */
@Component
@ConfigurationProperties(prefix = "vote")
public class VoteState {
    private int n;
    private int v;
    private int k;
    private int c;
    private int start;

    private int currentVoteNum;

    @PostConstruct
    public void init() {
        this.currentVoteNum = start;
    }

    // Config values set from application.yml or command line
    public int getN() { return n; }
    public void setN(int n) { this.n = n; }

    public int getV() { return v; }
    public void setV(int v) { this.v = v; }

    public int getK() { return k; }
    public void setK(int k) { this.k = k; }

    public int getC() { return c; }
    public void setC(int c) { this.c = c; }

    public int getStart() { return start; }
    public void setStart(int start) { this.start = start; }

    
}
