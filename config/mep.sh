#!/bin/bash

# COLOR
DEFAULT="\e[39m"
YELLOW="\e[93m"
GREEN="\e[32m"
BLUE="\e[34m"
RED="\e[91m"

CURRENT=`pwd`
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo -e $BLUE"Go to conf directory"$DEFAULT
cd $DIR

echo -ne $BLUE"The current version is : "$DEFAULT
version=$(grep "<version>" ../pom.xml | head -n 1 | cut -d '>' -f2 | cut -d '<' -f1)
echo -e $YELLOW$version$DEFAULT

echo -ne $BLUE"The new version is : "$DEFAULT
maj=$(echo $version | cut -d '.' -f1)
min=$(echo $version | cut -d '.' -f2)
new_version=$maj.$min.0
echo -e $YELLOW$new_version$DEFAULT

echo -ne $BLUE"Changing pom version ..."$DEFAULT
sed -i "s/$version/$new_version/g" ../pom.xml
echo -e $GREEN"OK"$DEFAULT

echo -ne $BLUE"Swapping Jenkinsfiles ..."$DEFAULT
mv Jenkinsfile Jenkinsfile.snapshot
mv Jenkinsfile.release Jenkinsfile
git add Jenkinsfile.snapshot
echo -e $GREEN"OK"$DEFAULT

echo -ne $BLUE"Commiting ... "$DEFAULT
git commit -am "[ UPD ] : Update version to $new_version"
echo -e $GREEN"OK"$DEFAULT

echo -ne $BLUE"Building ... "$DEFAULT
cd ..
mvn clean install || exit 1
cd $DIR
echo -e $GREEN"OK"$DEFAULT

echo -ne $BLUE"Tagging ... "$DEFAULT
git tag $new_version
echo -e $GREEN"OK"$DEFAULT

echo -ne $BLUE"Compute new version : "$DEFAULT
new_version_snapshot=$maj.$(($min+1)).0-SNAPSHOT
echo -e $YELLOW$new_version_snapshot$DEFAULT

echo -ne $BLUE"Swapping Jenkinsfiles ..."$DEFAULT
mv Jenkinsfile Jenkinsfile.release
mv Jenkinsfile.snapshot Jenkinsfile
sed -i "s/$new_version/$new_version_snapshot/g" ../pom.xml
git add Jenkinsfile.release
echo -e $GREEN"OK"$DEFAULT

echo -ne $BLUE"Commiting ... "$DEFAULT
git commit -am "[ UPD ] : Update version to $new_version_snapshot"
echo -e $GREEN"OK"$DEFAULT

cd $CURRENT



