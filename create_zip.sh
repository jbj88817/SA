#!/bin/bash

# Create a zip file of the project
zip -r SA.zip \
    app/src/ \
    app/build.gradle \
    build.gradle \
    gradle.properties \
    gradle/wrapper/ \
    gradlew \
    gradlew.bat \
    settings.gradle \
    README.md

echo "Project has been zipped to SA.zip" 