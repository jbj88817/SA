#!/bin/bash

# Create a zip file of the project with Kotlin DSL changes
zip -r SAKTS.zip \
    app/src/ \
    app/build.gradle.kts \
    build.gradle.kts \
    settings.gradle.kts \
    gradle/libs.versions.toml \
    gradle.properties \
    gradlew \
    gradlew.bat \
    gradle/wrapper/ \
    README.md \
    create_zip_kts.sh

echo "Project has been zipped to SAKTS.zip" 