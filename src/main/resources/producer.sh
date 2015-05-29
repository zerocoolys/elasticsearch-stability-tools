#!/usr/bin/env bash
bulk=5000

while getopts "b:" arg
    do
        case $arg in
            "b")
                bulk=$OPTARG
                ;;
            "?")
                echo "unknow argument"
                ;;
        esac
    done

java -cp .:./lib/*:./conf com.ss.ESDataGeneratorMain ${bulk}