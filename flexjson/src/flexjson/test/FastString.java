package flexjson.test;

import flexjson.JSONSerializer;

import java.io.*;

public final class FastString implements CharSequence {

    private char[] characters;
    private final int start;
    private final int end;

    public FastString( String str ) {
        characters = str.toCharArray();
        start = 0;
        end = characters.length;
    }

    public FastString(char[] characters) {
        this.characters = characters;
        start = 0;
        end = characters.length;
    }

    public FastString( char[] characters, int start, int end) {
        this.characters = characters;
        this.start = start;
        this.end = end;
    }

    public final int length() {
        return end - start;
    }

    public final char at( int i ) {
        return characters[i];
    }

    public final char charAt(int i) {
        return characters[i];
    }

    public final CharSequence subSequence(int s, int e) {
        return new FastString( characters, s, e );
    }

    public static CharSequence string(String value) {
        StringBuilder builder = new StringBuilder();
        builder.append('\"');
        int last = 0;
        int len = value.length();
        for( int i = 0; i < len; i++ ) {
            char c = value.charAt(i);
            if (c == '"') {
                builder.append(value, last, i ).append( "\\\"");
                last = i;
            } else if (c == '\\') {
                builder.append(value, last, i ).append("\\\\");
                last = i;
            } else if (c == '\b') {
                builder.append(value, last, i).append("\\b");
                last = i;
            } else if (c == '\f') {
                builder.append(value, last, i).append("\\f");
                last = i;
            } else if (c == '\n') {
                builder.append(value, last, i).append("\\n");
                last = i;
            } else if (c == '\r') {
                builder.append(value, last, i).append("\\r");
                last = i;
            } else if (c == '\t') {
                builder.append(value, last, i).append("\\t");
                last = i;
            } else if (Character.isISOControl(c)) {
                builder.append(value, last, i);
                last = i;
                unicode(builder, c);
            }
        }
        if( last < value.length() ) {
            builder.append( value, last, value.length() );
        }
        builder.append('\"');
        return builder;
    }

    private static void unicode(StringBuilder builder, char c) {
        builder.append("\\u");
        int n = c;
        for (int i = 0; i < 4; ++i) {
            int digit = (n & 0xf000) >> 12;
            builder.append(JSONSerializer.HEX[digit]);
            n <<= 4;
        }
    }

    public static CharSequence fastString(FastString value) {
        StringBuilder builder = new StringBuilder();
        builder.append('\"');
        int last = 0;
        int len = value.length();
        for( int i = 0; i < len; i++ ) {
            char c = value.at(i);
            if (c == '"') {
                builder.append(value, last, i ).append( "\\\"");
                last = i;
            } else if (c == '\\') {
                builder.append(value, last, i ).append("\\\\");
                last = i;
            } else if (c == '\b') {
                builder.append(value, last, i).append("\\b");
                last = i;
            } else if (c == '\f') {
                builder.append(value, last, i).append("\\f");
                last = i;
            } else if (c == '\n') {
                builder.append(value, last, i).append("\\n");
                last = i;
            } else if (c == '\r') {
                builder.append(value, last, i).append("\\r");
                last = i;
            } else if (c == '\t') {
                builder.append(value, last, i).append("\\t");
                last = i;
            } else if (Character.isISOControl(c)) {
                builder.append(value, last, i);
                last = i;
                unicode(builder, c);
            }
        }
        if( last < value.length() ) {
            builder.append( value, last, value.length() );
        }
        builder.append('\"');
        return builder;
    }


    public static void main(String[] args) throws IOException {
        File f = new File("/Users/charlie/email/mail/message.eml 3");
        System.out.println("Reading file " + f.getAbsolutePath() + "...");
        Reader reader = new BufferedReader( new FileReader( f ) );
        char[] content = new char[ (int)f.length() ];
        int start = 0;
        int len = 0;
        while( (len = reader.read( content, start, content.length - start ) ) > 0 ) {
            start += len;
        }

        for( int j = 0; j < 1000; j++ ) {

            int spaces = 0;
            String str = new String (content, 0, start );
            FastString fstr = new FastString( content, 0, start );

            long begin = System.nanoTime();
            for( int i = 0; i < str.length(); i++ ) {
                char c = str.charAt(i);
                if( c == ' ' ) spaces++;
            }
            long end = System.nanoTime();
            long stringtime = end-begin;

            spaces = 0;
            begin = System.nanoTime();
            for( int i = 0; i < fstr.length(); i++ ) {
                char c = fstr.charAt(i);
                if( c == ' ' ) spaces++;
            }
            end = System.nanoTime();
            long fasttime = end-begin;

            spaces = 0;
            begin = System.nanoTime();
            for( int i = 0; i < content.length; i++ ) {
                char c = content[i];
                if( c == ' ' ) spaces++;
            }
            end = System.nanoTime();
            long rawtime = end-begin;

            double improvement = 1.0 - (fasttime/(double)stringtime);
            System.out.println("Improvement: " + j + " " + improvement + "ns vs " + (1.0 - (rawtime / (double)stringtime )) + " ns");
        }


//        double totalImprovement = 0.0;
//        for( int i = 0; i < 100; i++ ) {
//            long begin = System.nanoTime();
//            string( str );
//            long end = System.nanoTime();
//            long stringtime = end-begin;
//
//            begin = System.nanoTime();
//            fastString( fstr );
//            end = System.nanoTime();
//            long fasttime = end-begin;
//
//            double improvement = 1.0 - (fasttime / (double)stringtime);
//            totalImprovement += improvement;
//            System.out.println("Improvement " + i + ": " + improvement + "ns" );
//        }
//        System.out.println( "Average Improvement: " + totalImprovement / 100 );
    }

}
