#!/usr/bin/env sh

sed -ir "s/^[#]*\s*version=.*/version=$1/" gradle.properties
git add . && git commit -m "Release version $1" &&
git tag $1 &&
git push origin && git push origin --tags