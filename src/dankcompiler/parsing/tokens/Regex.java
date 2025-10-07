package dankcompiler.parsing.tokens;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Regex {
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
    public int match(String source, int start){
        Matcher matcher = regex.matcher(source);
        matcher.region(start, source.length());
        if(matcher.lookingAt()&&start>=0){
            //this.match = source.substring(start, matcher.end());
            this.match = matcher.group(); 
            return matcher.end();
        }else{
            return -1;
        }
    }
}
