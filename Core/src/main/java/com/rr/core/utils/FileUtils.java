/*------------------------------------------------------------------------------
 * Copyright (c) 2015 Low Latency Trading Limited  :  Author Richard Rose
 ------------------------------------------------------------------------------*/
package com.rr.core.utils;

import com.rr.core.lang.ClockFactory;
import com.rr.core.lang.ErrorCode;
import com.rr.core.lang.ReusableString;
import com.rr.core.logger.ConsoleFactory;
import com.rr.core.logger.Level;
import com.rr.core.logger.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class FileUtils {

    public static final  int              FILE_COPY_BUF_SIZE = 4 * 1024 * 1024;
    private static final Logger _log = ConsoleFactory.console( FileUtils.class, Level.info );
    private static final SimpleDateFormat _fileDateFmt       = new SimpleDateFormat( "yyyyMMdd_HHmmss" );
    private static final ErrorCode        RENAME_ERR         = new ErrorCode( "FUT100", "Unable to rename file" );

    public static synchronized void mkDir( String dir ) throws FileException {

        if ( dir == null ) return;

        File file = new File( dir );

        if ( !file.exists() ) {

            boolean created = file.mkdirs();

            if ( !created ) {
                file = new File( dir );

                if ( !file.isDirectory() ) {
                    throw new FileException( "mkDir() failed to create dir " + dir );
                }
            }

        } else if ( !file.isDirectory() ) {
            throw new FileException( "mkDir() error " + dir + " exists and is not a directory" );
        }
    }

    public static void mkDirIfNeeded( String fileName ) throws FileException {
        String dir = getDirName( fileName );
        mkDir( dir );
    }

    public static void rm( String fileName ) throws FileException {
        File f = new File( fileName );

        if ( !f.exists() ) return;

        if ( !f.canWrite() ) throw new FileException( "rm() file is write protected " + fileName );

        if ( f.isDirectory() ) {
            String[] files = f.list();

            if ( files.length > 0 ) {
                throw new FileException( "rm() directory is not empty " + fileName );
            }
        }

        boolean deleted = f.delete();

        if ( !deleted ) throw new FileException( "Delete: deletion failed for " + fileName );

        _log.info( "DELETED : " + fileName );
    }

    public static Collection<Path> getMatchedPaths( String startDir, String filePatternMatch ) throws FileException {
        FileFinder finder = new FileFinder( filePatternMatch );

        try {
            File start     = getDIR( startDir );
            Path startPath = start.toPath();

            Files.walkFileTree( startPath, finder );
        } catch( Exception e ) {
            throw new FileException( "getMatchedPaths exception with startDir=" + startDir + ", filePtn=" + filePatternMatch + " : " + e.getMessage(), e );
        }

        return finder.getMatchedPaths();
    }

    public static void rmRecurse( String fileName, boolean log ) throws FileException {

        if ( fileName == null || fileName.length() <= 0 || fileName.equals( File.separator ) || fileName.equals( "." ) || fileName.equals( "." + File.separator ) ) {
            throw new FileException( "Saftety Precaution : Unable to remove root directory or current directory [" + fileName + "]" );
        }

        if ( log ) _log.info( "RECURSIVE DELETE : " + fileName );

        File f = new File( fileName );

        if ( !f.exists() ) return;

        if ( !f.canWrite() ) throw new FileException( "rm() file is write protected " + fileName );

        if ( f.isDirectory() ) {
            String[] files = f.list();

            for ( String child : files ) {
                rmRecurse( fileName + File.separator + child, log );
            }
        }

        boolean deleted = f.delete();

        if ( !deleted ) throw new FileException( "Delete: deletion failed" );
    }

    public static void archive( String fromFile, String destFile ) throws FileException {
        int    len;
        byte[] buf = new byte[ 8192 ];

        try {
            File inFile = new File( fromFile );

            if ( !inFile.exists() ) throw new FileException( "archive() file " + fromFile + " does not exist" );

            if ( !inFile.canWrite() ) throw new FileException( "archive() file " + fromFile + " is write protected so cant archive" );

            FileInputStream  in  = new FileInputStream( fromFile );
            GZIPOutputStream out = new GZIPOutputStream( new FileOutputStream( destFile ) );

            while( (len = in.read( buf )) > 0 ) {
                out.write( buf, 0, len );
            }

            in.close();

            out.finish();
            out.close();

            rm( fromFile );

        } catch( IOException e ) {
            throw new FileException( "archiveFile() from=" + fromFile + ", to=" + destFile + " failed " + e.getMessage(), e );
        }
    }

    public static void checkDirExists( String dir, boolean mustBeEmpty ) throws FileException {
        File f = new File( dir );

        if ( !f.exists() ) {
            throw new FileException( "checkDirExists() directory " + dir + " doesnt exist" );
        }

        if ( f.isDirectory() ) {
            String[] files = f.list();

            if ( mustBeEmpty && files.length > 0 ) {
                throw new FileException( "checkDirExists() directory " + dir + " is not empty " );
            }
        } else {
            throw new FileException( "checkDirExists() " + dir + " is NOT a directory" );
        }

        if ( !f.canWrite() ) throw new FileException( "checkDirExists() directory " + dir + " is write protected" );
    }

    public static boolean isDir( String dir, boolean mustBeWriteable ) throws FileException {
        File f = new File( dir );

        if ( !f.exists() ) return false;

        if ( !f.isDirectory() ) throw new FileException( "isDir() " + dir + " is NOT a directory" );

        if ( mustBeWriteable && !f.canWrite() ) throw new FileException( "isDir() directory " + dir + " is write protected" );

        return true;
    }

    public static void close( BufferedWriter bufferedWriter ) {
        try {
            if ( bufferedWriter != null ) {
                bufferedWriter.flush();
                bufferedWriter.close();
            }
        } catch( IOException e ) {
            // ignore
        }
    }

    public static void rmIgnoreError( String tmpFile ) {
        try {
            rm( tmpFile );
        } catch( FileException e ) {
            // ignore
        }
    }

    public static void rmForceRecurse( String tmpFile, boolean log ) {
        try {
            if ( tmpFile != null &&
                 !tmpFile.equals( File.separator ) &&
                 !tmpFile.equals( "." + File.separator ) &&
                 !tmpFile.equals( "." + File.separator + "." ) &&
                 !tmpFile.startsWith( "." + File.separator + ".." ) ) {

                rmRecurse( tmpFile, log );
            }
        } catch( FileException e ) {
            // ignore
        }
    }

    public static String getDirName( String name ) {
        int    idx = name.lastIndexOf( '/' );
        String dir = null;

        if ( idx != -1 ) {
            dir = name.substring( 0, idx );
        } else {
            idx = name.lastIndexOf( '\\' );

            if ( idx != -1 ) {
                dir = name.substring( 0, idx );
            }
        }

        return dir;
    }

    public static String[] getFiles( String pathRoots, String patternMatch ) {

        String[] fileNames;
        if ( pathRoots != null && pathRoots.length() > 1 && patternMatch != null && patternMatch.length() > 0 ) {

            String[] startDirs = pathRoots.split( "," );

            ArrayList<Path> allFiles = new ArrayList<>();

            for ( String startDir : startDirs ) {

                Collection<Path> paths = null;
                try {
                    paths = FileUtils.getMatchedPaths( startDir, patternMatch );
                } catch( FileException e ) {
                    throw new SMTRuntimeException( "FileUtils.getFilesIn .. error getting matched paths : " + e.getMessage(), e );
                }

                allFiles.addAll( paths );
            }

            if ( allFiles.size() == 0 ) throw new SMTRuntimeException( "getFilesIn() failed to match any files for pathRoots=" + pathRoots + ", ptn=" + patternMatch );

            fileNames = new String[ allFiles.size() ];

            int idx = 0;

            for ( Path p : allFiles ) {
                fileNames[ idx++ ] = p.toFile().getPath();
            }
        } else {
            fileNames = new String[ 0 ];
        }

        return fileNames;
    }

    public static String[] getFiles( String pathWithFilePtns ) {

        String[] fileNames;

        if ( pathWithFilePtns != null && pathWithFilePtns.length() > 0 ) {

            ArrayList<Path> allFiles = new ArrayList<>();

            String[] entries = StringUtils.split( pathWithFilePtns, ',' );

            for ( String pathWithFilePtn : entries ) {

                int idx = pathWithFilePtn.lastIndexOf( '/' );

                if ( idx == -1 ) pathWithFilePtn.lastIndexOf( '\\' );

                if ( idx == -1 ) return new String[ 0 ];

                String startDir = pathWithFilePtn.substring( 0, idx );
                String pattern  = pathWithFilePtn.substring( idx + 1 );

                Collection<Path> paths = null;
                try {
                    paths = FileUtils.getMatchedPaths( startDir, pattern );
                } catch( FileException e ) {
                    throw new SMTRuntimeException( "FileUtils.getFiles .. error getting matched paths : " + e.getMessage(), e );
                }

                allFiles.addAll( paths );
            }

            if ( allFiles.size() == 0 ) throw new SMTRuntimeException( "getFilesIn() failed to match any files for " + pathWithFilePtns );

            fileNames = new String[ allFiles.size() ];

            int idx = 0;

            for ( Path p : allFiles ) {

                if ( p.toFile().isFile() ) {
                    fileNames[ idx++ ] = p.toFile().getPath();
                }
            }
        } else {
            fileNames = new String[ 0 ];
        }

        return fileNames;
    }

    public static String formRollableFileName( String fname, int rollNumber, String extension ) {

        String date;

        synchronized( _fileDateFmt ) {
            date = _fileDateFmt.format( new Date() ); // DOESNT USE BACKTEST CLOCK
        }

        String base = fname;

        int idx = base.indexOf( extension );

        if ( idx > 0 ) {
            base = base.substring( 0, idx );
        }

        return String.format( "%s_%s_%03d%s", base, date, rollNumber, extension );
    }

    public static boolean backup( String fname ) {
        String bkupName = formRollableFileName( fname, 1, ".bkup" );

        File file = new File( fname );

        try {
            if ( !file.exists() ) {
                return true;
            }

            rmIgnoreError( bkupName );

            File bkupFile = new File( bkupName );

            if ( file.renameTo( bkupFile ) ) {
                return true;
            }

            _log.error( RENAME_ERR, " " + fname + " to " + bkupName );

        } catch( Exception e ) {
            _log.error( RENAME_ERR, " " + fname + " to " + bkupName, e );
        }

        return false;
    }

    public static boolean move( String fromFile, String toFile ) {

        try {
            String bkupFile = toFile + ".bkUp";

            File fileSrc  = new File( fromFile );
            File fileDest = new File( toFile );
            File fileBkup = new File( bkupFile );

            rmIgnoreError( bkupFile );

            if ( fileDest.exists() ) {
                if ( !fileDest.renameTo( fileBkup ) ) {
                    _log.error( RENAME_ERR, " " + toFile + " to " + bkupFile );
                    return false;
                }
            }

            if ( fileSrc.renameTo( fileDest ) ) {

                rmIgnoreError( bkupFile );

                return true;
            }

            _log.error( RENAME_ERR, " " + fromFile + " to " + toFile );

        } catch( Exception e ) {
            _log.error( RENAME_ERR, " " + fromFile + " to " + toFile + " " + e.getMessage(), e );
        }

        return false;
    }

    public static boolean renameDir( String dirNameOld, String dirNameNew ) {
        File file = new File( dirNameOld );

        try {
            File bkupFile = new File( dirNameNew );

            if ( file.renameTo( bkupFile ) ) {
                return true;
            }

            _log.warn( "Unable to rename directory " + dirNameOld + " to " + dirNameNew );

        } catch( Exception e ) {
            _log.error( RENAME_ERR, " " + dirNameOld + " to " + dirNameNew, e );
        }

        return false;
    }

    public static boolean isFile( String fName ) {
        if ( fName == null ) return false;

        fName = fName.trim();

        File file = new File( fName );

        if ( !file.isFile() || !file.canRead() ) {
            final URL resource = ClassLoader.getSystemResource( fName );
            if ( resource != null ) file = new File( resource.getFile() );
        }

        return file.exists() && file.isFile();
    }

    public static void close( Closeable resource ) {
        try {
            if ( resource != null ) {
                resource.close();
            }
        } catch( IOException e ) {
            // ignore
        }
    }

    public static boolean readFiles( List<String> lines, String fileList ) throws IOException {
        lines.clear();

        String[] files = fileList.split( "," );

        boolean expanded = false;

        for ( String fileName : files ) {
            expanded |= doRead( lines, fileName, true, true, null );
        }

        return expanded;
    }

    /**
     * read the file and return array of String with each line having an array entry
     * <p>
     * To link in parts of files in parent file use
     *
     * @param lines           list to put each line read from file, WILL BE CLEARED FIRST
     * @param fileName
     * @param stripComments   if true then exclude all lines starting with '#'
     * @param stripWhiteSpace if true strip out blank lines and whitespace around the lines
     * @return true if any file expansion via @INCLUDE occurred
     * @throws IOException
     * @INCLUDE {fileToInclude}[optionalId]
     * <p>
     * In include file to use sections use
     * @SECTION {id} START
     * .....
     * @SECTION {id} END
     */
    public static boolean read( List<String> lines, String fileName, boolean stripComments, boolean stripWhiteSpace ) throws IOException {
        lines.clear();

        return doRead( lines, fileName, stripComments, stripWhiteSpace, null );
    }

    private static boolean doRead( List<String> lines, String fileName, boolean stripComments, boolean stripWhiteSpace, String sectionName ) throws IOException {

        if ( fileName == null ) throw new IOException( "Missing filename" );

        boolean expanded    = false;
        boolean justSection = sectionName != null;
        boolean inSection   = sectionName == null;

        BufferedReader rdr = bufFileReader( fileName );

        _log.info( "FileUtils.read  file " + fileName );

        try {
            int count = 0;

            String line;
            int    lineNo = 0;

            while( (line = rdr.readLine()) != null ) {
                ++lineNo;

                if ( stripWhiteSpace ) line = line.trim();

                String trimLine = line.trim();

                if ( justSection ) {
                    if ( trimLine.startsWith( "@SECTION" ) ) {
                        String[] parts = trimLine.split( " " );

                        if ( parts.length == 3 ) {
                            String id       = parts[ 1 ];
                            String startEnd = parts[ 2 ];

                            if ( sectionName.equals( id ) && "START".equalsIgnoreCase( startEnd ) ) {
                                inSection = true;
                                continue;
                            } else if ( sectionName.equals( id ) && "END".equalsIgnoreCase( startEnd ) ) {
                                _log.info( "FileUtils.read subSection " + sectionName + " from " + fileName + ", entries=" + count );
                                return true;
                            }
                        } else {
                            throw new IOException( "Bad @SECTION directive in " + fileName + ", expected @SECTION {id} {START|END}, not " + line );
                        }
                    }
                }

                if ( inSection ) {
                    if ( trimLine.startsWith( "@INCLUDE" ) ) {
                        String[] parts = trimLine.split( " " );

                        if ( parts.length == 2 ) {
                            String subFile = parts[ 1 ];
                            try {
                                doRead( lines, subFile, stripComments, stripWhiteSpace, null );
                            } catch( IOException e ) {
                                throw new IOException( "Exception processing " + fileName + " [" + lineNo + "] : " + e.getMessage(), e );
                            }
                            expanded = true;
                        } else if ( parts.length == 3 ) {
                            String subFile    = parts[ 1 ];
                            String subSection = parts[ 2 ];
                            try {
                                doRead( lines, subFile, stripComments, stripWhiteSpace, subSection );
                            } catch( IOException e ) {
                                throw new IOException( "Exception processing " + fileName + " [" + lineNo + "] : " + e.getMessage(), e );
                            }
                            expanded = true;
                        } else {
                            throw new IOException( "Bad @INCLUDE directive in " + fileName + " [" + lineNo + "]  args=" + parts.length + " expected {fileName} {optionalSection} not : " + line );
                        }

                    } else if ( trimLine.startsWith( "#" ) && stripComments ) {
                        // strip
                    } else if ( trimLine.length() == 0 && stripWhiteSpace ) {
                        // strip
                    } else {
                        ++count;
                        lines.add( line );
                    }
                }
            }

            if ( justSection ) {
                if ( inSection ) throw new IOException( "Missing @SECTION " + sectionName + " END   directive in " + fileName );

                throw new IOException( "Missing @SECTION " + sectionName + " START   directive in " + fileName );
            }

            _log.info( "FileUtils.read " + fileName + ", entries=" + count );

        } finally {
            FileUtils.close( rdr );
        }

        return expanded;
    }

    public static boolean expand( String fileNameIn, String expandedFile, boolean stripComments, boolean stripWhiteSpace ) throws IOException {

        if ( fileNameIn == null ) throw new IOException( "Missing src filename" );
        if ( expandedFile == null ) throw new IOException( "Missing expanded filename" );

        List<String> lines = new ArrayList<>();

        boolean expanded = read( lines, fileNameIn, stripComments, stripWhiteSpace );

        if ( expanded ) Files.write( Paths.get( expandedFile ), lines );

        return expanded;
    }

    public static BufferedReader bufFileReader( String fName ) throws IOException {
        fName = fName.trim();

        File file = getFile( fName );

        if ( fName.endsWith( ".gz" ) || fName.endsWith( ".gzip" ) ) {
            GZIPInputStream gzip = new GZIPInputStream( new FileInputStream( file ) );

            return new BufferedReader( new InputStreamReader( gzip ) );
        }

        return new BufferedReader( new FileReader( file ) );
    }

    public static BufferedReader bufFileReader( String fName, int size ) throws IOException {
        fName = fName.trim();

        File file = getFile( fName );

        if ( fName.endsWith( ".gz" ) || fName.endsWith( ".gzip" ) ) {
            GZIPInputStream gzip = new GZIPInputStream( new FileInputStream( file ) );

            return new BufferedReader( new InputStreamReader( gzip ) );
        }

        return new BufferedReader( new FileReader( file ), size );
    }

    public static BufferedWriter bufFileWriter( String fName ) throws Exception {
        return bufFileWriter( fName, false );
    }

    public static BufferedWriter bufFileWriter( String fName, boolean append ) throws Exception {
        fName = fName.trim();

        mkDirIfNeeded( fName );

        File file = new File( fName );

        if ( fName.endsWith( ".gz" ) || fName.endsWith( ".gzip" ) ) {
            GZIPOutputStream gzip = new GZIPOutputStream( new FileOutputStream( file, append ), true );

            return new BufferedWriter( new OutputStreamWriter( gzip ) );
        }

        return new BufferedWriter( new FileWriter( file, append ) );
    }

    public static BufferedWriter bufFileWriter( String fName, int size ) throws Exception {
        return bufFileWriter( fName, size, false );
    }

    public static BufferedWriter bufFileWriter( String fName, int size, boolean append ) throws Exception {
        fName = fName.trim();

        mkDirIfNeeded( fName );

        File file = new File( fName );

        if ( fName.endsWith( ".gz" ) || fName.endsWith( ".gzip" ) ) {
            GZIPOutputStream gzip = new GZIPOutputStream( new FileOutputStream( file, append ), true );

            return new BufferedWriter( new OutputStreamWriter( gzip ) );
        }

        return new BufferedWriter( new FileWriter( file, append ), size );
    }

    public static BufferedOutputStream bufFileOutStream( String fName, int bufSize ) throws Exception {
        return bufFileOutStream( fName, bufSize, false );
    }

    public static BufferedOutputStream bufFileOutStream( String fName, int bufSize, boolean append ) throws Exception {
        fName = fName.trim();

        mkDirIfNeeded( fName );

        if ( fName.endsWith( ".gz" ) || fName.endsWith( ".gzip" ) ) {
            GZIPOutputStream gzip = new GZIPOutputStream( new FileOutputStream( fName, append ) );

            return new ZBufferedOutputStream( gzip, bufSize );
        }

        return new ZBufferedOutputStream( new FileOutputStream( fName, append ), bufSize );
    }

    public static BufferedInputStream bufFileInpStream( String fName ) throws IOException {
        fName = fName.trim();

        File file = getFile( fName );

        if ( fName.endsWith( ".gz" ) || fName.endsWith( ".gzip" ) ) {
            GZIPInputStream gzip = new GZIPInputStream( new FileInputStream( file ) );

            return new BufferedInputStream( gzip );
        }

        return new BufferedInputStream( new FileInputStream( file ) );
    }

    public static BufferedInputStream bufFileInpStream( String fName, int bufSize ) throws IOException {
        fName = fName.trim();

        File file = getFile( fName );

        if ( fName.endsWith( ".gz" ) || fName.endsWith( ".gzip" ) ) {
            GZIPInputStream gzip = new GZIPInputStream( new FileInputStream( file ) );

            return new BufferedInputStream( gzip, bufSize );
        }

        return new BufferedInputStream( new FileInputStream( file ), bufSize );
    }

    public static long getLastModifiedTS( final String fName ) {
        try {
            File file = getFile( fName );

            return file.lastModified();

        } catch( Exception e ) {
            return 0;
        }
    }

    public static long getCreatedTS( final String fName ) {

        try {
            File file = getFile( fName );

            Path path = Paths.get( file.toURI() );

            BasicFileAttributes attr = Files.readAttributes( path, BasicFileAttributes.class );

            _log.log( Level.debug, "FileUtils.getCreatedTS " + fName + " createTime " + attr.creationTime() );

            return attr.creationTime().toMillis();

        } catch( Exception e ) {

            return 0;
        }
    }

    public static File getFile( String fName ) {
        fName = fName.trim();

        File file = new File( fName );

        if ( !file.isFile() || !file.canRead() ) {
            final URL resource = ClassLoader.getSystemResource( fName );
            if ( resource != null ) file = new File( resource.getFile() );
        }

        if ( !file.isFile() || !file.canRead() ) {

            ClassLoader cl = ClassLoader.getSystemClassLoader();

            URL[] urls = ((URLClassLoader) cl).getURLs();

            _log.info( "CLASSPATH" );
            for ( URL url : urls ) {
                _log.info( url.getFile() );
            }

            throw new SMTRuntimeException( "File doesnt exist or is not readable " + fName + ", isFile=" + file.isFile() + ", canRead=" + file.canRead() );
        }

        return file;
    }

    public static File getDIR( String dirName ) {
        dirName = dirName.trim();

        File dir = new File( dirName );

        if ( !dir.isFile() || !dir.canRead() ) {
            final URL resource = ClassLoader.getSystemResource( dirName );
            if ( resource != null ) dir = new File( resource.getFile() );
        }

        if ( !dir.isDirectory() || !dir.canRead() ) {
            throw new SMTRuntimeException( "Directory doesnt exist or is not readable " + dirName );
        }

        return dir;
    }

    /**
     * Reads all the bytes from a file. The method ensures that the file is
     * closed when all bytes have been read or an I/O error, or other runtime
     * exception, is thrown.
     *
     * <p> Note that this method is intended for simple cases where it is
     * convenient to read all bytes into a byte array. It is not intended for
     * reading in large files.
     */
    public static String fileToString( String fName ) throws IOException {
        fName = fName.trim();

        BufferedReader bufFileReader = bufFileReader( fName );

        String line;

        StringBuilder sb = new StringBuilder();

        while( (line = bufFileReader.readLine()) != null ) {
            sb.append( line );
            sb.append( "\n" );
        }

        return sb.toString();
    }

    public static String getBaseName( final String fileName ) {
        String baseName = fileName;
        int    idx      = fileName.lastIndexOf( '/' );
        if ( idx == -1 ) {
            idx = fileName.lastIndexOf( '\\' );
        }
        if ( idx > 0 && idx < fileName.length() ) {
            baseName = fileName.substring( idx + 1 );
        }
        return baseName;
    }

    public static List<String> getDirEntries( String dir ) throws FileException {
        File f = new File( dir );

        List<String> entries = new ArrayList<>();

        if ( !f.exists() ) throw new FileException( "checkDirExists() directory " + dir + " doesnt exist" );

        if ( f.isDirectory() ) {
            String[] files = f.list();

            for ( String s : files ) {
                entries.add( s );
            }
        } else {
            throw new FileException( "checkDirExists() " + dir + " is NOT a directory" );
        }

        return entries;
    }

    public static void writeFile( String fileName, String data ) throws FileException, IOException {
        mkDirIfNeeded( fileName );

        Path path = Paths.get( fileName );

        Files.write( path, data.getBytes() );
    }

    public static void flush( final OutputStream os ) {
        try {
            os.flush();
        } catch( IOException e ) {
            _log.warn( "flush error : " + e.getMessage() );
        }
    }

    public static void gzip( final String fileName ) {

        if ( fileName.endsWith( ".gz" ) ) return;

        int bufSize = FILE_COPY_BUF_SIZE;

        long started = ClockFactory.getLiveClock().currentTimeMillis();

        _log.info( "Zipping " + fileName );

        boolean failed = false;

        try( BufferedInputStream fis = bufFileInpStream( fileName, bufSize );
             BufferedOutputStream fos = bufFileOutStream( fileName + ".gz", bufSize ); ) {

            byte[] buffer = new byte[ bufSize ];
            int    len;

            while( (len = fis.read( buffer )) > 0 ) {
                fos.write( buffer, 0, len );
            }

        } catch( Exception e ) {
            _log.warn( "Error compressing " + fileName + " : " + e.getMessage() );

            failed = true;
        }

        if ( !failed ) {
            rmIgnoreError( fileName );
        }

        long ended = ClockFactory.getLiveClock().currentTimeMillis();

        _log.info( "Zip " + fileName + " took " + (ended - started) + "ms" );
    }

    public static void readStreamToEnd( final InputStream in, ReusableString out ) throws IOException {
        BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );
        String         line   = "";

        while( (line = reader.readLine()) != null ) {
            out.append( line );
        }
    }

    public static Map<String, String> fileToMap( final String fileName, char delim ) {
        final List<String> lines = new ArrayList<>();

        final HashMap<String, String> map = new HashMap<>();

        try {
            readFiles( lines, fileName );
        } catch( IOException e ) {
            _log.info( "Cant read " + fileName + " " + e.getMessage() );
        }

        final ArrayList<String> bits = new ArrayList<>();

        for ( String line : lines ) {
            bits.clear();

            StringUtils.split( line, delim, bits );

            if ( bits.size() != 2 ) {
                throw new SMTRuntimeException( "fileToMap: " + fileName + " line [" + line + "]  doesnt have two pieces using '" + delim + "' as delim" );
            }

            map.put( bits.get( 0 ), bits.get( 1 ) );
        }

        return map;
    }
}
