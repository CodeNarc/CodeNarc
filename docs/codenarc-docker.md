---
layout: default
title: CodeNarc - Docker
---

# CodeNarc - Docker

## Docker Hub

[Docker Hub](https://hub.docker.com/) includes [several images](https://hub.docker.com/search?q=codenarc&type=image) 
that execute CodeNarc, with various capabilities and versions. 

## The CodeNarc Organization "Official" CodeNarc Image

The CodeNarc organization on Docker Hub provides a simple [CodeNarc image](https://hub.docker.com/r/codenarc/codenarc) 
that executes the CodeNarc *command-line* (starting with CodeNarc 2.0.0).

Some sample Docker `run` commands (substitute the desired CodeNarc image version):

    docker run --rm -v `pwd`:/ws --user `id -u`:`id -g` codenarc/codenarc:2.0.0-groovy2.5.12
            
    # Assumes there is a "codenarc.ruleset" file in the current directory
    docker run --rm -v `pwd`:/ws --user `id -u`:`id -g` codenarc/codenarc:2.0.0-groovy2.5.12  -rulesetfiles=file:codenarc.ruleset

    # Assumes there is a "codenarc.properties" file in the current directory
    docker run --rm -v `pwd`:/ws --user `id -u`:`id -g` codenarc/codenarc:2.0.0-groovy2.5.12  -properties=file:codenarc.properties

    # Write out violations report as text to stdout
    docker run --rm -v `pwd`:/ws --user `id -u`:`id -g` codenarc/codenarc:2.0.0-groovy2.5.12 -rulesetfiles=file:codenarc.ruleset -report=text:stdout

See the [CodeNarc-command line](https://codenarc.org/codenarc-command-line.html) page for a description of the CodeNarc
command-line syntax and options.

This Docker image runs CodeNarc against the `/ws` directory, so these commands mount the current directory to `/ws`.
These commands also run the image with the current user, to avoid permission issues on file access and creation.
