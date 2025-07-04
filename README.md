# Mario - A Super Mario Game and Editor
![Java Build](https://github.com/bobbylight/mario/actions/workflows/gradle.yml/badge.svg)
![Java Build](https://github.com/bobbylight/mario/actions/workflows/codeql-analysis.yml/badge.svg)

This is a Super Mario Bros. game and editor I built many years ago.  Nostalgia
goggles are encouraging me to clean things up and put this on GitHub. It tries to
recreate the original Super Mario Bros. using Super Mario World's graphics.

**The libraries used for this project are archaic**, but maybe the code will inspire
someone to build something cool. Th e OG game was written in [Slick](https://slick.ninjacave.com/) (see the `mario-slick` subproject), which relied on [LWJGL](https://www.lwjgl.org/) 2.x. This code still works, but Slick is no longer maintained and LWJGL is on version 3.x, so I'd like to refactor this to have a version of the game built on a newer library or framework such as ligbdx.

See the `README.md` in each subproject for more information:

* [mario-common](mario-common/README.md) - Resources shared by all sister subprojects
* [mario-slick](mario-slick/README.md) - The game itself, written in Slick, which 
  uses LWJGL for rendering
* [level-editor](level-editor/README.md) - The level editor used to make the game's
  levels
