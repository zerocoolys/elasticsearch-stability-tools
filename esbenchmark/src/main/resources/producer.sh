#!/usr/bin/env bash
bulk=5000
startOffset=-30
endOffset=-1

while getopts "b:s:e:" arg
    do
        case $arg in
            "b")
                bulk=$OPTARG
                ;;
            "s")
                startOffset=$OPTARG
                ;;
            "e")
                endOffset=$OPTARG
                ;;
            "?")
                echo "unknow argument"
                ;;
        esac
    done

java -cp .:./lib/*:./conf com.ss.ESDataGeneratorMain ${bulk} ${startOffset} ${endOffset}