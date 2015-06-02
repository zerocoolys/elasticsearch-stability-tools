#!/usr/bin/env bash
bulk=50000
startOffset=-1
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

java -Xms2g -Xmx2g -cp .:./lib/*:./conf com.ss.EsDataGeneratorMain ${bulk} ${startOffset} ${endOffset}