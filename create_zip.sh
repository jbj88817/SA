#!/bin/bash

# Create a zip file of the project
zip -r SA.zip \
    app/src/ \
    app/build.gradle.kts \
    build.gradle.kts \
    settings.gradle.kts \
    gradle/libs.versions.toml \
    gradle.properties \
    gradle/wrapper/ \
    gradlew \
    gradlew.bat \
    README.md

echo "Project has been zipped to SA.zip" 