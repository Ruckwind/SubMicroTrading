package com.rr.core.session.file;

import java.util.*;

import static java.lang.Character.isDigit;

public class FileNameDateGrouper {

    private static final Character YEAR_DELIM1 = '_';
    private static final Character YEAR_DELIM2 = '-';

    /**
     * group related files in order of date
     *
     * @param filesIn
     * @return
     */
    public static String[] groupFilesByDate( final String[] filesIn ) {
        Map<String, Set<String>> groups = new TreeMap<>();

        for ( String fileName : filesIn ) {
            String group = findGroup( fileName );

            Set<String> l = groups.computeIfAbsent( group, ( a ) -> new HashSet<>() );

            l.add( fileName );
        }

        String[] out = new String[ groups.size() ];

        int idx = 0;

        for ( String grp : groups.keySet() ) {
            Set<String> set = groups.get( grp );

            List<String> vals = new ArrayList<>( set );

            vals.sort( null );

            StringBuilder sb = new StringBuilder();

            for ( String s : vals ) {
                if ( sb.length() > 0 ) sb.append( "," );
                sb.append( s.trim() );
            }

            out[ idx++ ] = sb.toString();
        }

        return out;
    }

    private static String findGroup( final String s ) {

        int len = s.length();

        if ( len < 4 ) return s;

        int maxLoop = len - 3;
        for ( int idx = 0; idx < maxLoop; idx++ ) {
            Character y1 = s.charAt( idx );
            Character y2 = s.charAt( idx + 1 );
            Character y3 = s.charAt( idx + 2 );
            Character y4 = s.charAt( idx + 3 );

            if ( isDigit( y1 ) && isDigit( y2 ) && isDigit( y3 ) && isDigit( y4 ) ) {
                if ( (y1 == '1' && y2 == '9') || (y1 == '2' && y2 == '0') ) { // found YYYY

                    if ( (idx + 6) > len ) { // no room for _MM ... group just by year
                        return s.substring( 0, idx ) + s.substring( idx + 4 );
                    }

                    Character c5 = s.charAt( idx + 4 );

                    int nextIdx = idx + 4;

                    if ( c5 == YEAR_DELIM1 || c5 == YEAR_DELIM2 ) ++nextIdx;

                    Character m1 = s.charAt( nextIdx++ );
                    Character m2 = s.charAt( nextIdx++ );

                    if ( !isDigit( m1 ) || !isDigit( m2 ) ) return s.substring( 0, idx ) + s.substring( idx + 4 ); // NOT MONTH JUST PROC YEAR

                    int im1 = m1 - '0';
                    int im2 = m2 - '0';

                    int month = im1 * 10 + im2;

                    if ( month < 1 || month > 12 ) return s.substring( 0, idx ) + s.substring( idx + 4 ); // NOT VALID MONTH JUST PROC YEAR

                    if ( (nextIdx + 3) > len ) return s.substring( 0, idx ) + s.substring( nextIdx );

                    int dayIdx = nextIdx;

                    if ( c5 == YEAR_DELIM1 || c5 == YEAR_DELIM2 ) ++dayIdx;

                    Character d1 = s.charAt( dayIdx++ );
                    Character d2 = s.charAt( dayIdx++ );

                    if ( !isDigit( d1 ) || !isDigit( d2 ) ) return s.substring( 0, idx ) + s.substring( nextIdx ); // NOT DAY JUST PROC YEAR MONTH

                    int id1 = d1 - '0';
                    int id2 = d2 - '0';

                    int day = id1 * 10 + id2;

                    if ( day < 1 || day > 31 ) return s.substring( 0, idx ) + s.substring( nextIdx ); // NOT VALID DAY JUST PROC YEAR MONTH

                    return s.substring( 0, idx ) + s.substring( dayIdx );
                }
            }
        }

        return s;
    }
}
