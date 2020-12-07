# README for CodeNarc Dockerfile

To build the image:

    docker build -t codenarc .
    # or
    docker build -t codenarc --build-arg CODENARC_VERSION=1.6.1 --build-arg GROOVY_VERSION=3.0.6 .

To run:

    docker run --rm -v `pwd`:/ws --user `id -u`:`id -g` codenarc
            
    # or (assumes there is a "codenarc.ruleset" file in the current directory)
    docker run --rm -v `pwd`:/ws --user `id -u`:`id -g` codenarc -report=json -rulesetfiles=file:codenarc.ruleset
