# Kollektist

This project was born when I wanted to try out Kotlin and, at the same 
time, was perpetually annoyed with the buggy quick-add functionality
of 
    [Todoist](https://todoist.com) 
(in Firefox). So I set out to build a quick-add bar/program for all 
platforms (there was no official one for GNU/Linux at that point).

The web application has since been improved, so I will probably never
finish this. The existing code *is* functional, though: using a 
rudimentary CLI interface, you can create tasks in your Todoist account!

## Usage

**Important:** This is not robust software -- use at your own risk!

 1. While logged in to your Todoist account, visit the
        [Todoist App Console](https://developer.todoist.com/appconsole.html)
    and create a new token. Remember to keep this secret!
    
 2. Clone the Kollektist repository and run 
 
        mvn clean; mvn package
        
 3. Copy `target/kollektist-0.1-jar-with-dependencies.jar` where you
    need it, and rename it to please you.
    We will call it `kollektist.jar` below.
    
 4. Now you can run Kollektist:
 
        java -jar kollektist.jar --api-token=<token> \
                                 --backend=todoist \
                                 --frontend=cli

    Follow the prompts to add a new task; separate labels with commas
    or spaces. Do not add `#` or `@`. Note that, for whatever reason,
    I encoded priorities the wrong way around, so `1` is lowest and 
    `4` is highest.
    
    *Warning:* There is no error handling whatsoever, so if anything
    goes wrong you will have to wade through command-line dumps.
   

## Kollektist as Daemon

When run as

    java -jar kollektist.jar --api-token=<token> \
                             --backend=todoist \
                             --frontend=files \
                             --loop
                            
Kollektist will pick up JSON files of a certain form
from the current folder, read tasks from them, and
push them to Todoist. You can create such files with
another instance using the files backend, 

    java -jar kollektist.jar --api-token=<token> \
                             --backend=files \
                             --frontend=cli

or you own software. The idea here was, of course,
to implement a GUI frontend for Kollektist, which
would get labels and projects from the daemon and
create new task files.

Have fun experimenting with this
