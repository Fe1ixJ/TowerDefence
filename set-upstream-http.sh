#!/bin/bash

# This may require that you have already committed your current state

# Add the original "forked" repository with the name "httpupstream"
git remote add httpupstream https://gitlab.liu.se/jonkv82/javaoo-base.git

# Check out the current main branch
git checkout main

# Fetch any new commits from the main branch of the "httpupstream" repository
git fetch httpupstream

# Merge those commits into the current main branch.
# This should result in you having to write a merge commit message.
git pull httpupstream main
