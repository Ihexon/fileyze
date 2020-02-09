# Watcher

Watcher is a simple file status monitor service

## usage

```bash
WatchMe - A util use to monitor the changes of directory
usage:
watchme [OPTION] [DIRECTOR]
-r                  : recurse monitor, means the all directory and file will be monitor
-d [dir]                : the directory to monitor
--exclude-hidden   : exclude the hidden directory
--log [file]         : log message to file
--help & -h        : show help
```

On Linux



```bash
ihexon@gentoo$ chmod +x watchme
ihexon@gentoo$ ./watchme -r -d `$DIR` --log /tmp/log
```

Other Platform

```bash
java -jar Watchme.jar -r -d $PATH --log /tmp/log
```

[Please read project Wikis](https://github.com/ihexon/Watcher/wiki/WatchMe%E6%96%87%E6%A1%A3)

## Contribute

Platform **JDK >= 8**

Recommend to Use Intellij IDEA, ISSUE ME PLEASE

Contributer: [IHEXON](https://github.com/ihexon), [FlyingMcDonald](https://github.com/FlyingMcDonald)
