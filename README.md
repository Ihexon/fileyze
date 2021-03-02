# Watcher


 
Watcher is a simple file status monitor service.
## usage

```bash
WatchMe - A util use to monitor the changes of directory
usage:
watchme [OPTION] [DIRECTOR]
-r                      : recurse monitor, means the all directory and file will be monitor
-d [dir]                : the directory to monitor
-exchid                 : exclude the hidden directory
--log [file]            : log message to file
--help & -h             : show help
-ex [syntax:pattern]    : exclude path or file with pattern [syntax:pattern]
```

## about -ex `[syntax:pattern]` command

A FileSystem implementation supports the "glob" and "regex" syntaxes, and may support others. The value of the syntax component is compared without regard to case.
When the syntax is "glob" then the String representation of the path is matched using a limited pattern language that resembles regular expressions but with a simpler syntax. For example:

- Whe syntax is glob:
```
[syntax:pattern]:

*.java
Matches a path that represents a file name ending in .java

*.*
Matches file names containing a dot

*.{java,class}
Matches file names ending with .java or .class

foo.?
Matches file names starting with foo. and a single character extension
/home/*/*
Matches /home/gus/data on UNIX platforms
/home/**
Matches /home/gus and /home/gus/data on UNIX platforms

```

- when syntax is regex
```
regex:^.*/\\~[^/]*$
```
Should ignore scratch files

## Deploy on Linux

```bash
ihexon@gentoo$ chmod +x watchme
ihexon@gentoo$ ./watchme -r -d `$DIR` --log /tmp/log -ex glob:**.exe
```

Other Platform

```bash
java -jar Watchme.jar -r -d $PATH --log /tmp/log -ex glob:**.exe
```

[Please read project Wikis](https://github.com/ihexon/Watcher/wiki/WatchMe%E6%96%87%E6%A1%A3)

## Contribute

    It just a TOY I guess

**THE THINGS MUST TO KNOW:**
 1. **KEEP IT F\*\*K SIMPLE,CLEAN,STUPID**
 2. **NO F\*\*KING COMPLEX CODE PLEASE**

Platform **JDK >= 8**

Recommend to Use Intellij IDEA
Using jprofiler [Java profiler]( https://www.ej-technologies.com/products/jprofiler/overview.html)


Contributer: [IHEXON](https://github.com/ihexon), [FlyingMcDonald](https://github.com/FlyingMcDonald)


