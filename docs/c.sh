#!/bin/bash

echo "Converting $1"

filename=$1.md

#if [ -f $filename ]; then
#    echo "[$filename] already exists; conversion canceled."
#    exit 1
#fi

cp src/site/apt/$1.apt $filename

sed -i 's/<<</`/g' $filename
sed -i 's/>>>/`/g' $filename

sed -i 's/<</**/g' $filename
sed -i 's/>>/**/g' $filename

sed -i 's/</*/g' $filename
sed -i 's/>/*/g' $filename

sed -i 's/^\* /## /g' $filename
sed -i 's/^\*\* /### /g' $filename
sed -i 's/^\*\*\* /#### /g' $filename

sed -i 's/+----------------------------------*/```/g' $filename


sed -i 's/{{{/zzz/g' $filename
sed -i 's/}}/yy/g' $filename
#sed -i 's/}/qq/g' $filename

# sed -ir 's/zzz\(.*\)qq\(.*\)yy/@@\1@@\2@@/g' $filename        -- worked
sed -i 's/zzz\(.*\)}\(.*\)yy/[\2](\1)/g' $filename


# {{{./codenarc-developer-guide.html#The_codenarc_Command-line_Script}<<codenarc create-rule>>}}
