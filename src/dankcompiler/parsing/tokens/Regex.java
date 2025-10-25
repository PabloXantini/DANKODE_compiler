package dankcompiler.parsing.tokens;

import java.util.regex.Pattern;

import dankcompiler.parsing.rdutils.Cursor;

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
    public int match(String source, Cursor cursor){
        Matcher matcher = regex.matcher(source);
        int start = cursor.getValue();
        matcher.region(start, source.length());
        if(matcher.lookingAt()&&start>=0){
            this.match = matcher.group();
            cursor.advance(matcher.end(), match.length());
            return matcher.end();
        }else{
            return -1;
        }
    }
}
