package com.rr.core.session.file;

import com.rr.core.lang.BaseTestCase;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class FileNameDateGrouperTest extends BaseTestCase {

    @Test public void noGrouping() {
        String[] filesIn     = { "aaa.001", "bbb.001" };
        String[] expFilesOut = { "aaa.001", "bbb.001" };

        String[] filesOut = FileNameDateGrouper.groupFilesByDate( filesIn );

        assertArrayEquals( expFilesOut, filesOut );
    }

    @Test public void singleFile() {
        String[] filesIn     = { "aaa.001", "bbb.001" };
        String[] expFilesOut = { "aaa.001", "bbb.001" };

        String[] filesOut = FileNameDateGrouper.groupFilesByDate( filesIn );

        assertArrayEquals( expFilesOut, filesOut );
    }

    @Test public void yyyyAtEndWithTwoSets() {
        String[] filesIn     = { "xxx2017", "xxx2016", "aaa2016", "aaa2018" };
        String[] expFilesOut = { "aaa2016,aaa2018", "xxx2016,xxx2017" };

        String[] filesOut = FileNameDateGrouper.groupFilesByDate( filesIn );

        assertArrayEquals( expFilesOut, filesOut );
    }

    @Test public void yyyyAtStart() {
        String[] filesIn     = { "2017aaa.001", "2016aaa.001" };
        String[] expFilesOut = { "2016aaa.001,2017aaa.001" };

        String[] filesOut = FileNameDateGrouper.groupFilesByDate( filesIn );

        assertArrayEquals( expFilesOut, filesOut );
    }

    @Test public void yyyyAtStartWithTwoSets() {
        String[] filesIn     = { "2017aaa.001", "2016aaa.001", "2016bbb.001", "2018bbb.001" };
        String[] expFilesOut = { "2016aaa.001,2017aaa.001", "2016bbb.001,2018bbb.001" };

        String[] filesOut = FileNameDateGrouper.groupFilesByDate( filesIn );

        assertArrayEquals( expFilesOut, filesOut );
    }

    @Test public void yyyyInMiddleWithTwoSets() {
        String[] filesIn     = { "xxx2017aaa.001", "xxx2016aaa.001", "xxx2016bbb.001", "xxx2018bbb.001" };
        String[] expFilesOut = { "xxx2016aaa.001,xxx2017aaa.001", "xxx2016bbb.001,xxx2018bbb.001" };

        String[] filesOut = FileNameDateGrouper.groupFilesByDate( filesIn );

        assertArrayEquals( expFilesOut, filesOut );
    }

    @Test public void yyyyMMDDInMiddleWithTwoSets() {
        String[] filesIn     = { "xxx2017_08_01aaa.001", "xxx2016_09_30aaa.001", "xxx2016_03_01bbb.001", "xxx2018_01_30bbb.001" };
        String[] expFilesOut = { "xxx2016_09_30aaa.001,xxx2017_08_01aaa.001", "xxx2016_03_01bbb.001,xxx2018_01_30bbb.001" };

        String[] filesOut = FileNameDateGrouper.groupFilesByDate( filesIn );

        assertArrayEquals( expFilesOut, filesOut );
    }

    @Test public void yyyyMMDDInMiddleWithTwoSetsSameYear() {
        String[] filesIn     = { "xxx2016_09_30aaa.001", "xxx2016_09_01aaa.001", "xxx2016_05_01bbb.001", "xxx2016_01_30bbb.001" };
        String[] expFilesOut = { "xxx2016_09_01aaa.001,xxx2016_09_30aaa.001", "xxx2016_01_30bbb.001,xxx2016_05_01bbb.001" };

        String[] filesOut = FileNameDateGrouper.groupFilesByDate( filesIn );

        assertArrayEquals( expFilesOut, filesOut );
    }

    @Test public void yyyyMMInMiddleWithTwoSets() {
        String[] filesIn     = { "xxx2017_08aaa.001", "xxx2016_09aaa.001", "xxx2016_03bbb.001", "xxx2018_01bbb.001" };
        String[] expFilesOut = { "xxx2016_09aaa.001,xxx2017_08aaa.001", "xxx2016_03bbb.001,xxx2018_01bbb.001" };

        String[] filesOut = FileNameDateGrouper.groupFilesByDate( filesIn );

        assertArrayEquals( expFilesOut, filesOut );
    }
}