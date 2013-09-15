#!/bin/sh

if diff $1 $2 >/dev/null ; then
    echo "$1 and $2 are the same"
else
    echo "$1 and $2 are different"
fi

