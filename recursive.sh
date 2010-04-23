function action {
    curDir=$1
    echo "Processing directory '$curDir'"
    rm $curDir/*.orig
}

function recursive {
    curDir=$1
    
    action $curDir
    for file in $curDir/*; do
        if [ -d $file ]; then
            recursive $file
        fi
    done
}

recursive .

