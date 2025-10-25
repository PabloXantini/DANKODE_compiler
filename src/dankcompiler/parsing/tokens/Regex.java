package dankcompiler.parsing.tokens;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Regex {
    private int end = -1;
    private String match;
    private final Pattern regex;
    public Regex(String sregex){
        regex = Pattern.compile(sregex);
    }
    public Pattern getPattern(){
        return regex;
    }
    public String getMatch(){
        return this.match;
    }
    public int getEnd(){
        return this.end;
    }
    public int match(String source, int start){
        Matcher matcher = regex.matcher(source);
        matcher.region(start, source.length());
        if(matcher.lookingAt()&&start>=0){
            this.match = matcher.group();
            this.end = matcher.end();
            return end;
        }else{
            return -1;
        }
    }
}
