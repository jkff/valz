#!/bin/bash

#check for uncommited changes
if [[ $(hg st | wc -l) -gt 0 ]]; then
    echo "There are uncommited changes. Please, commit all local files before closing branch."
    exit
fi

BRANCH=$1

hg up $BRANCH
hg ci -m"Close branch $BRANCH" --close-branch
hg up default
hg merge $BRANCH
hg ci -m"Merged $BRANCH with default"

