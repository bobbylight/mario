# mario-slick - A Super Mario Bros. Game
Written with lwjgl/[slick](https://slick.ninjacave.com/) many years ago.  Perhaps this could be used as the
basis for a libgdx implementation.

## Running
The easiest way is to run through an IDE that understands how to run a Gradle
app, just be sure to set the path to the LWJGL natives.  For example:

```
-Djava.library.path=mario-slick/lib/native/windows
```

To build and run an executable package in `build/install/mario` for your OS:

```bash
./gradlew build installDist
cd mario-slick/build/install/mario
./bin/mario # bin/mario.bat on Windows
```

To build the executable package for a different OS, specify the proper
subdirectory for the natives:

```bash
./gradlew build installDist -PnativesSubdir=windows
./gradlew build installDist -PnativesSubdir=macosx
./gradlew build installDist -PnativesSubdir=solaris
```

## To-Do/Bugs
See the [GitHub Issues](https://github.com/bobbylight/mario/issues) for the latest.