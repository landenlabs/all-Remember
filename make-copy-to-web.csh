

./gradlew assemble
cp app/build/outputs/apk/debug/app-debug.apk ~/private/websites/landenlabs-ipage/android/all-uitest/

./gradlew clean
rm all-uitest-src.zip
source make-zip.csh
cp all-uitest-src.zip ~/private/websites/landenlabs-ipage/android/all-uitest/
 
