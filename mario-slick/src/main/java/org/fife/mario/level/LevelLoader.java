package org.fife.mario.level;

import java.io.IOException;

import org.fife.mario.Axe;
import org.fife.mario.FireStick;
import org.fife.mario.Goal;
import org.fife.mario.Mario;
import org.fife.mario.MovingPlatform;
import org.fife.mario.Position;
import org.fife.mario.Springboard;
import org.fife.mario.Toad;
import org.fife.mario.WarpInfo;
import org.fife.mario.anim.WarpingAnimation;
import org.fife.mario.blocks.Block;
import org.fife.mario.blocks.BlockTypes;
import org.fife.mario.blocks.ClayBlock;
import org.fife.mario.blocks.CoinContent;
import org.fife.mario.blocks.FireFlowerContent;
import org.fife.mario.blocks.IceBlock;
import org.fife.mario.blocks.ImmovableBlock;
import org.fife.mario.blocks.LoadedClayBlock;
import org.fife.mario.blocks.MusicBlock;
import org.fife.mario.blocks.OneUpContent;
import org.fife.mario.blocks.QuestionBlock;
import org.fife.mario.blocks.StarContent;
import org.fife.mario.blocks.TextBlock;
import org.fife.mario.blocks.TextContent;
import org.fife.mario.blocks.LoadedBlock.Content;
import org.fife.mario.enemy.Bowser;
import org.fife.mario.enemy.FlyingKoopaTroopa;
import org.fife.mario.enemy.Goomba;
import org.fife.mario.enemy.KoopaTroopa;
import org.fife.mario.enemy.PiranhaPlant;
import org.newdawn.slick.SlickException;


/**
 * Loads a level from a file.
 *
 * @author Robert Futrell
 * @version 1.0
 */
final class LevelLoader {


    /**
     * Private constructor to prevent instantiation.
     */
    private LevelLoader() {
        // Do nothing (message for Sonar).
    }

	private static Content getContentForString(String str) throws IOException {

		Content c;

		if (str.startsWith("coin")) {
			try {
				int count = Integer.parseInt(str.substring(4));
				c = new CoinContent(count);
			} catch (NumberFormatException nfe) {
				throw new IOException("Bad content: " + str);
			}
		}

		else if ("fireflower".equals(str)) {
			c = new FireFlowerContent();
		}

		else if ("feather".equals(str)) {
			// TODO
			throw new IOException("'feather' content not yet supported");
		}

		else if ("star".equals(str)) {
			c = new StarContent();
		}

		else if ("oneup".equals(str)) {
			c = new OneUpContent();
		}

		else if ("poison".equals(str)) {
			// TODO
			throw new IOException("'poison' content not yet supported");
		}

		else {
			throw new IOException("Unknown content: " + str);
		}

		return c;

	}


	/**
	 * Loads a level from a text file.
	 *
	 * @param fileName The file to load from.
	 * @param level The level being loaded.
	 * @throws IOException If an IO error occurs.
	 */
	public static void load(String fileName, Level level) throws IOException {

		LevelFileReader r = new LevelFileReader(fileName);

		try {

			String areaCountStr = r.readKeyValueLine("AreaCount");
			int areaCount = Integer.parseInt(areaCountStr);

			String temp = r.readKeyValueLine("MarioStart");
			if (temp.length()>0) {
				String[] params = temp.split(",");
				if (params.length!=3) {
					throw new IOException("Invalid MarioStart: " + temp);
				}
				int row = Integer.parseInt(params[1]);
				int col = Integer.parseInt(params[2]);
				Mario mario = Mario.get();
				float x = col*32;
				float y = row*32;
				level.setStartingLocation(x, y);
System.out.println("... ... ... setting starting location: " + x + ", " + y);
				if ("WarpingAnimation".equals(params[0])) {
					WarpInfo info = new WarpInfo("main");
					info.setStartPosition(new Position(row, col));
					int imgY = mario.getImageRow();
					WarpingAnimation anim = new WarpingAnimation(mario, x,y, 0,imgY, info, null);
					anim.setGoingOut(true);
					level.setStartingAnimation(anim);
				}
				else if ("None".equals(params[0])) {
					level.setStartingAnimation(null);
				}
				else {
					throw new IOException("Invalid anim for MarioStart: " + temp);
				}
			}

			level.clearAreas();
			for (int i=0; i<areaCount; i++) {
				String name = r.readKeyValueLine("Area");
				Area area = loadArea(r);
				level.addArea(name, area);
			}

		} finally {
			r.close();
		}

	}


	private static Area loadArea(LevelFileReader r) throws IOException {

		// Flags describing the behavior of the area
		boolean flyingFish = false;
		boolean water = false;
		String temp = r.readKeyValueLine("Flags");
		if (temp.length()>0) {
			String[] flags = temp.split(",");
            for (String flag : flags) {
                if ("flying_fish".equals(flag)) {
                    flyingFish = true;
                }
                else if ("water".equals(flag)) {
                    water = true;
                }
            }
		}

		// Create an area of the right dimensions
		String[] dims = r.readLine().split("\\s+");
		int rowCount = Integer.parseInt(dims[0]);
		int colCount = Integer.parseInt(dims[1]);
		MapData data = new MapData(rowCount, colCount);
		Area area = new Area(data);

		// Set any flags for the area.
		area.setFlyingFish(flyingFish);
		area.setWater(water);

		// The background image.
		String bgImageName = r.readLine().trim();
		area.setBackgroundImage(bgImageName);

		String tileset = r.readLine().trim();
		try {
			area.setTileset(tileset);
		} catch (SlickException se) {
			throw new IOException(se);
		}

		int music = Integer.parseInt(r.readLine().trim());
		area.setMusic(music);

		loadBlocks(r, area, data);
		loadEnemies(r, area, data);
		loadWarps(r, area, data);
		loadOther(r, area, data);

		// Load tile info for the actual level
		for (int row=0; row<rowCount; row++) {
			String line = r.readLine();
			String[] cols = line.split("\\s+");
			if (cols.length!=colCount) {
			    throw new IOException("Invalid terrain file (for row " + row + ", " + cols.length + "!=" +
                    colCount + ")");
			}
			for (int col=0; col<cols.length; col++) {
				int terrain = Integer.parseInt(cols[col]);
				if (terrain<0) {
					throw new IOException("Invalid terrain in level file: " + terrain);
				}
				data.setTerrainAllLayers(row, col, terrain);
			}
		}

		return area;

	}


	private static void loadBlocks(LevelFileReader r, Area area, MapData data)
									throws IOException {

		int blockCount = Integer.parseInt(r.readLine());
		int blockIndex = 0;

		for (int i=0; i<blockCount; i++) {
			String line = r.readLine();
			String prefix = "block " + (i+1);
			if (!line.startsWith(prefix)) {
				throw new IOException("Invalid block line: '" + line + "'");
			}
			String[] parms = line.substring(prefix.length()).trim().split("\\s+");
			if (parms.length<2) {
				throw new IOException("Invalid block line: '" + line + "'");
			}
			String[] loc = parms[0].split(",");
			int row = Integer.parseInt(loc[0]);
			int col = Integer.parseInt(loc[1]);
			boolean hidden = false;
			String typeName = parms[1];
			BlockTypes type = Enum.valueOf(BlockTypes.class, typeName);
			switch (type) {
				case BLOCK_MUSIC_NOTE:
					Block b = new MusicBlock(area);
					b.setRowAndColumn(row, col);
					data.addBlock(row, col, b);
					blockIndex++;
					break;
				case BLOCK_YELLOW:
				case BLOCK_BLUE:
				case BLOCK_GRAY:
					Content content = null;
					if (parms.length>2) {
						String temp = parms[2];
						int comma = temp.indexOf(',');
						if (comma>-1) {
							String contentStr = temp.substring(0, comma);
							content = getContentForString(contentStr);
							hidden = "hidden".equals(temp.substring(comma+1));
						}
						else {
							content = getContentForString(temp);
						}
					}
					b = content==null ? new ClayBlock(area, type) :
						new LoadedClayBlock(area, content, type, hidden);
					b.setRowAndColumn(row, col);
					data.addBlock(row, col, b);
					blockIndex++;
					break;
				case BLOCK_QUESTION:
				case BLOCK_QUESTION_RED:
					content = null;
					if (parms.length>2) {
						String temp = parms[2];
						int comma = temp.indexOf(',');
						if (comma>-1) {
							String contentStr = temp.substring(0, comma);
							content = getContentForString(contentStr);
							hidden = "hidden".equals(temp.substring(comma+1));
						}
						else {
							content = getContentForString(temp);
						}
					}
					b = new QuestionBlock(area, content, type, hidden);
					b.setRowAndColumn(row, col);
					data.addBlock(row, col, b);
					blockIndex++;
					break;
				case BLOCK_YELLOW_COIN:
				case BLOCK_BLUE_COIN:
					// TODO: Blue coins currently not supported
					data.addCoin(row, col);
					blockIndex++;
					break;
				case BLOCK_SOLID_BROWN:
				case BLOCK_ROCK:
					b = new ImmovableBlock(area, type);
					b.setRowAndColumn(row, col);
					data.addBlock(row, col, b);
					blockIndex++;
					break;
				case BLOCK_INFORMATION:
					int start = line.indexOf('"');
					int end = line.indexOf('"', start+1);
					String text = line.substring(start+1, end);
					text = text.replaceAll("\\\\n", "\n");
					content = new TextContent(text);
					b = new TextBlock(area, content);
					b.setRowAndColumn(row, col);
					data.addBlock(row, col, b);
					blockIndex++;
					break;
				case BLOCK_ICE:
					b = new IceBlock(area);
					b.setRowAndColumn(row, col);
					data.addBlock(row, col, b);
					blockIndex++;
					break;
			}
		}

		// Sanity check
		if (blockIndex!=blockCount) {
			throw new IOException("Expected " + blockCount +
								" blocks, found " + blockIndex);
		}

	}


	private static void loadEnemies(LevelFileReader r, Area area, MapData data)
									throws IOException {

		int enemyCount = Integer.parseInt(r.readLine());
		//int enemyIndex = 0;

		for (int i=0; i<enemyCount; i++) {

			String line = r.readLine();
			String[] parms = line.split("\\s+");
			if (parms.length<2) {
				throw new IOException("Invalid enemy line: '" + line + "'");
			}

			String[] loc = parms[0].split(",");
			int row = Integer.parseInt(loc[0]);
			int col = Integer.parseInt(loc[1]);
			float x = col*32;
			float y = row*32;

			org.fife.mario.Character c;

			try {
				String type = parms[1];
				if ("goomba".equals(type)) {
					c = new Goomba(area, x, y);
				}
				else if ("koopatroopa".equals(type)) {
					int color = KoopaTroopa.COLOR_GREEN;
					if (parms.length>2) {
						String temp = parms[2];
						if ("red".equals(temp)) {
							color = KoopaTroopa.COLOR_RED;
						}
						else if ("blue".equals(temp)) {
							color = KoopaTroopa.COLOR_BLUE;
						}
						else if ("yellow".equals(temp)) {
							color = KoopaTroopa.COLOR_YELLOW;
						}
					}
					c = new KoopaTroopa(area, x, y, color);
				}
				else if ("flying_koopatroopa".equals(type)) {
					int color = KoopaTroopa.COLOR_GREEN;
					if (parms.length>2) {
						String temp = parms[2];
						if ("red".equals(temp)) {
							color = KoopaTroopa.COLOR_RED;
						}
						else if ("blue".equals(temp)) {
							color = KoopaTroopa.COLOR_BLUE;
						}
						else if ("yellow".equals(temp)) {
							color = KoopaTroopa.COLOR_YELLOW;
						}
					}
					c = new FlyingKoopaTroopa(area, x, y, color);
				}
				else if ("piranha_plant".equals(type)) {
					boolean up = parms.length>=3 && "up".equals(parms[2]);
					x += 16; // In the "middle" of a pipe.
					c = new PiranhaPlant(area, x, y, up);
				}
				else if ("bowser".equals(type)) {
					c = new Bowser(area, x, y);
				}
				else if ("axe".equals(type)) {
					c = new Axe(area, x, y);
				}
				else if ("toad".equals(type)) {
					c = new Toad(area, x, y);
				}
				else if ("moving_platform".equals(type)) {
					boolean up = parms.length>=3 && "up".equals(parms[2]);
					c = new MovingPlatform(area, x, y, up);
				}
				else {
					throw new IOException("Unknown enemy type: " + type);
				}
			} catch (SlickException se) {
				throw new IOException(se);
			}

			area.addCharacter(c);

		}

	}


	private static void loadWarps(LevelFileReader r, Area area, MapData data)
								throws IOException {

		int warpCount = Integer.parseInt(r.readLine());

		for (int i=0; i<warpCount; i++) {

			String line = r.readLine();
			String[] parms = line.split("\\s+");
			if (parms.length<2) {
				throw new IOException("Invalid warp line: '" + line + "'");
			}

			String[] loc = parms[0].split(",");
			int row = Integer.parseInt(loc[0]);
			int col = Integer.parseInt(loc[1]);
			Position warpPos = new Position(row, col);

			String[] info = parms[1].split(",");
			WarpInfo wi = new WarpInfo(info[0]);
			row = Integer.parseInt(info[1]);
			col = Integer.parseInt(info[2]);
			wi.setStartPosition(new Position(row, col));
			area.addWarp(warpPos, wi);

		}

	}


	private static void loadOther(LevelFileReader r, Area area, MapData data)
			throws IOException {

		int otherCount = Integer.parseInt(r.readLine());

		for (int i = 0; i < otherCount; i++) {

			String line = r.readLine();
			String[] parms = line.split("\\s+");
			if (parms.length < 2) {
				throw new IOException("Invalid other line: '" + line + "'");
			}

			String[] loc = parms[0].split(",");
			int row = Integer.parseInt(loc[0]);
			int col = Integer.parseInt(loc[1]);
			float x = col * 32;
			float y = row * 32;

			String type = parms[1];
			if ("goal".equals(type)) {
				Goal goal = new Goal(area, x, y);
				area.addGoal(goal);
			}
			else if ("firestick".equals(type)) {
				FireStick fs = new FireStick(x, y, area);
				area.fireSticks.add(fs);
			}
			else if ("springboard".equals(type)) {
				Springboard sb = new Springboard(area, x, y);
				area.addCharacter(sb);
			}
			else {
				throw new IOException("Unknown other type: " + type);
			}

		}

	}

}
