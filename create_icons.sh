#!/bin/bash

# Create directories if they don't exist
mkdir -p app/src/main/res/mipmap-mdpi
mkdir -p app/src/main/res/mipmap-hdpi
mkdir -p app/src/main/res/mipmap-xhdpi
mkdir -p app/src/main/res/mipmap-xxhdpi
mkdir -p app/src/main/res/mipmap-xxxhdpi

# Check if ImageMagick is installed
if command -v convert >/dev/null 2>&1; then
  # Create transparent PNGs using ImageMagick
  convert -size 48x48 xc:transparent app/src/main/res/mipmap-mdpi/ic_launcher.png
  convert -size 48x48 xc:transparent app/src/main/res/mipmap-mdpi/ic_launcher_round.png

  convert -size 72x72 xc:transparent app/src/main/res/mipmap-hdpi/ic_launcher.png
  convert -size 72x72 xc:transparent app/src/main/res/mipmap-hdpi/ic_launcher_round.png

  convert -size 96x96 xc:transparent app/src/main/res/mipmap-xhdpi/ic_launcher.png
  convert -size 96x96 xc:transparent app/src/main/res/mipmap-xhdpi/ic_launcher_round.png

  convert -size 144x144 xc:transparent app/src/main/res/mipmap-xxhdpi/ic_launcher.png
  convert -size 144x144 xc:transparent app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png

  convert -size 192x192 xc:transparent app/src/main/res/mipmap-xxxhdpi/ic_launcher.png
  convert -size 192x192 xc:transparent app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png
else
  # If ImageMagick is not available, download sample icons
  curl -s -o app/src/main/res/mipmap-mdpi/ic_launcher.png https://raw.githubusercontent.com/google/android-material-design-icons/master/png/action/account_circle/materialicons/48dp/1x/baseline_account_circle_black_48dp.png
  cp app/src/main/res/mipmap-mdpi/ic_launcher.png app/src/main/res/mipmap-mdpi/ic_launcher_round.png
  
  curl -s -o app/src/main/res/mipmap-hdpi/ic_launcher.png https://raw.githubusercontent.com/google/android-material-design-icons/master/png/action/account_circle/materialicons/48dp/1.5x/baseline_account_circle_black_48dp.png
  cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-hdpi/ic_launcher_round.png
  
  curl -s -o app/src/main/res/mipmap-xhdpi/ic_launcher.png https://raw.githubusercontent.com/google/android-material-design-icons/master/png/action/account_circle/materialicons/48dp/2x/baseline_account_circle_black_48dp.png
  cp app/src/main/res/mipmap-xhdpi/ic_launcher.png app/src/main/res/mipmap-xhdpi/ic_launcher_round.png
  
  curl -s -o app/src/main/res/mipmap-xxhdpi/ic_launcher.png https://raw.githubusercontent.com/google/android-material-design-icons/master/png/action/account_circle/materialicons/48dp/3x/baseline_account_circle_black_48dp.png
  cp app/src/main/res/mipmap-xxhdpi/ic_launcher.png app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png
  
  curl -s -o app/src/main/res/mipmap-xxxhdpi/ic_launcher.png https://raw.githubusercontent.com/google/android-material-design-icons/master/png/action/account_circle/materialicons/48dp/4x/baseline_account_circle_black_48dp.png
  cp app/src/main/res/mipmap-xxxhdpi/ic_launcher.png app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png
fi

echo "Icon files created successfully!"
