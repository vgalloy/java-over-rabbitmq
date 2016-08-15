#!/bin/bash

version=$(grep "<version>" ../pom.xml | head -n 1 | cut -d '>' -f2 | cut -d '<' -f1) 
maj=$(echo $version | cut -d '.' -f1)
min=$(echo $version | cut -d '.' -f2)
new_version=$maj.$min.0
echo "new_version : "$new_version

sed -i "s/$version/$new_version/g" ../pom.xml

mv Jenkinsfile Jenkinsfile.snapshot
mv Jenkinsfile.release Jenkinsfile
git add Jenkinsfile.snapshot

git commit -am "[ UPD ] : Update version to $new_version"
git tag $new_version
#(cd .. && mvn clean install -DskipTests)
new_version_snapshot=$maj.$(($min+1)).0-SNAPSHOT

mv Jenkinsfile Jenkinsfile.release
mv Jenkinsfile.snapshot Jenkinsfile
sed -i "s/$new_version/$new_version_snapshot/g" ../pom.xml
git add Jenkinsfile.release
git commit -am "[ UPD ] : Update version to $new_version_snapshot"

