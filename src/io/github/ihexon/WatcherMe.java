package io.github.ihexon;
import io.github.ihexon.output.NullOutputStream;

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class WatcherMe {

    private final WatchService watcher;
    private final Map<WatchKey,Path> keys;
    private final boolean recursive;
    private PrintStream out;
    private PrintStream outFile;
    private List<String> specialFile;
    private SimpleDateFormat currentDate;
    private boolean trace = false;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        String dirStr = dir.toString();
        //不为特殊文件的话，将该文件注册监听，否则不做处理
        if(!specialFile.contains(dirStr)){
            WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            if (trace) {
                Path prev = keys.get(key);
                if (prev == null) {
                    System.out.format("register: %s\n", dir);
                } else {
                    if (!dir.equals(prev)) {
                        System.out.format("update: %s -> %s\n", prev, dir);
                    }
                }
            }
            keys.put(key, dir);
        }else{
            System.out.println("Warning：This dir contains special dir："+dirStr+"\n"+"Special dir will skip");
        }

    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        try {
            Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                        throws IOException
                {
                    register(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }catch (AccessDeniedException e){
            System.err.println("Error！ Existing file or folder permissions are insufficient to monitor："+e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    WatcherMe(Path dir, boolean recursive) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey,Path>();
        this.recursive = recursive;
        this.specialFile = new ArrayList<String>(Arrays.asList("", "This list for Special file "));
        this.out = System.out;
        this.outFile = new PrintStream("log.txt");
        this.currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (recursive) {
            System.out.format("Scanning %s ...\n", dir);
            registerAll(dir);
            System.out.println("Done.");
        } else {
            register(dir);
        }

        // enable trace after initial registration
        this.trace = true;
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() {
        System.setOut(outFile);
        while(true) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }
            List<WatchEvent<?>> eventList = key.pollEvents();

            for (WatchEvent<?> event: eventList) {
                WatchEvent.Kind<?> kind = event.kind();
                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;

                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // print out event to log file

                System.out.format("%s: %s: %s\n", currentDate.format(new Date()), event.kind().name(), child);


                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
        System.setOut(out);
    }

    static void usage() {
        System.err.println("usage: java WatchDir [-r] dir");
        System.exit(-1);
    }

    public static void main(String[] args) throws Exception {
        // parse arguments
        if (args.length == 0 || args.length > 2)
            usage();
        boolean recursive = false;
        int dirArg = 0;
        if (args[0].equals("-r")) {
            if (args.length < 2)
                usage();
            recursive = true;
            dirArg++;
        }


        // register directory and process its events
        Path dir = Paths.get(args[dirArg]);
        new WatcherMe(dir, recursive).processEvents();
    }



    private static void setCustomErrStream() {
        System.setErr(
                new DelegatorPrintStream(System.err) {

                    @Override
                    public void println(String x) {
                        // Suppress Nashorn removal warnings, too verbose (a warn each time is
                        // used).
                        if ("Warning: Nashorn engine is planned to be removed from a future JDK release"
                                .equals(x)) {
                            return;
                        }
                        super.println(x);
                    }
                });
    }


    private static class DelegatorPrintStream extends PrintStream {

        private final PrintStream delegatee;

        public DelegatorPrintStream(PrintStream delegatee) {
            super(NullOutputStream.NULL_OUTPUT_STREAM);
            this.delegatee = delegatee;
        }

        @Override
        public void flush() {
            delegatee.flush();
        }

        @Override
        public void close() {
            delegatee.close();
        }

        @Override
        public boolean checkError() {
            return delegatee.checkError();
        }

        @Override
        protected void setError() {
            // delegatee manages its error state.
        }

        @Override
        protected void clearError() {
            // delegatee manages its error state.
        }

        @Override
        public void write(int b) {
            delegatee.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            delegatee.write(b);
        }

        @Override
        public void write(byte buf[], int off, int len) {
            delegatee.write(buf, off, len);
        }

        @Override
        public void print(boolean b) {
            delegatee.print(b);
        }

        @Override
        public void print(char c) {
            delegatee.print(c);
        }

        @Override
        public void print(int i) {
            delegatee.print(i);
        }

        @Override
        public void print(long l) {
            delegatee.print(l);
        }

        @Override
        public void print(float f) {
            delegatee.print(f);
        }

        @Override
        public void print(double d) {
            delegatee.print(d);
        }

        @Override
        public void print(char s[]) {
            delegatee.print(s);
        }

        @Override
        public void print(String s) {
            delegatee.print(s);
        }

        @Override
        public void print(Object obj) {
            delegatee.print(obj);
        }

        @Override
        public void println() {
            delegatee.println();
        }

        @Override
        public void println(boolean x) {
            delegatee.println(x);
        }

        @Override
        public void println(char x) {
            delegatee.println(x);
        }

        @Override
        public void println(int x) {
            delegatee.println(x);
        }

        @Override
        public void println(long x) {
            delegatee.println(x);
        }

        @Override
        public void println(float x) {
            delegatee.println(x);
        }

        @Override
        public void println(double x) {
            delegatee.println(x);
        }

        @Override
        public void println(char x[]) {
            delegatee.println(x);
        }

        @Override
        public void println(String x) {
            delegatee.println(x);
        }

        @Override
        public void println(Object x) {
            delegatee.println(x);
        }

        @Override
        public PrintStream printf(String format, Object... args) {
            return delegatee.printf(format, args);
        }

        @Override
        public PrintStream printf(Locale l, String format, Object... args) {
            return delegatee.printf(l, format, args);
        }

        @Override
        public PrintStream format(String format, Object... args) {
            delegatee.format(format, args);
            return this;
        }

        @Override
        public PrintStream format(Locale l, String format, Object... args) {
            delegatee.format(l, format, args);
            return this;
        }

        @Override
        public PrintStream append(CharSequence csq) {
            delegatee.append(csq);
            return this;
        }

        @Override
        public PrintStream append(CharSequence csq, int start, int end) {
            delegatee.append(csq, start, end);
            return this;
        }

        @Override
        public PrintStream append(char c) {
            delegatee.append(c);
            return this;
        }
    }
}

