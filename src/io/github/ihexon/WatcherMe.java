package io.github.ihexon;
import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.io.*;
import java.text.DateFormat;
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

    public static void main(String[] args) throws IOException {
        // parse arguments
        if (args.length == 0 || args.length > 2)
            usage();
<<<<<<< HEAD
        boolean recursive = true;
=======
        boolean recursive = false;
>>>>>>> upstream/master
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
}

