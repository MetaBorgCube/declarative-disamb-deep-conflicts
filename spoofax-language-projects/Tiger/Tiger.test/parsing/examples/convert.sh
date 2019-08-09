#!/bin/bash

for i in *.tig; do
	
	name=$(echo $i | cut -f 1 -d '.')
	echo "converting $name"
    value=$(<$i)
    echo "module $name

language Tiger start symbol Module

test $name [[$value]] parse succeeds" >> "$name.spt"
rm $i
done