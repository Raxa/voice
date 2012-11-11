#!/bin/bash

python makeDB.py -f "$1.train" >> "$1.txt";

file=$PWD/"$1.txt";
text2wfreq < "$1.txt"|wfreq2vocab > "$1.vocab";
text2idngram -vocab "$1.vocab" -idngram "$1.idngram" < "$1.txt";
idngram2lm -vocab_type 0 -idngram "$1.idngram" -vocab "$1.vocab" -arpa "$1.arpa";
