#!/bin/bash

# Copy and update main source files
for file in $(find app/src/main/java/com/example/intuitrepos -type f | grep -v "IntuitReposApplication.kt"); do
    # Get the relative path within the package
    rel_path=${file#app/src/main/java/com/example/intuitrepos/}
    # Create the new file path
    new_file="app/src/main/java/com/example/sa/$rel_path"
    # Copy the file with updated package name
    sed 's/package com.example.intuitrepos/package com.example.sa/g' "$file" > "$new_file"
    # Update any references to the old package
    sed -i '' 's/com.example.intuitrepos/com.example.sa/g' "$new_file"
    # Update any references to IntuitRepos in class names or comments
    sed -i '' 's/IntuitRepos/SA/g' "$new_file"
    echo "Updated: $new_file"
done

# Copy and update test files
for file in $(find app/src/test/java/com/example/intuitrepos -type f); do
    # Get the relative path within the package
    rel_path=${file#app/src/test/java/com/example/intuitrepos/}
    # Create the new file path
    new_file="app/src/test/java/com/example/sa/$rel_path"
    # Copy the file with updated package name
    sed 's/package com.example.intuitrepos/package com.example.sa/g' "$file" > "$new_file"
    # Update any references to the old package
    sed -i '' 's/com.example.intuitrepos/com.example.sa/g' "$new_file"
    # Update any references to IntuitRepos in class names or comments
    sed -i '' 's/IntuitRepos/SA/g' "$new_file"
    echo "Updated: $new_file"
done

echo "Project renamed from IntuitRepos to SA successfully!" 