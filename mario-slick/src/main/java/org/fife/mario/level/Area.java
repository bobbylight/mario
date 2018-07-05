package org.fife.mario.level;

import org.fife.mario.*;
import org.fife.mario.Animation;
import org.fife.mario.Character;
import org.fife.mario.anim.CoinGrabbedAnimation;
import org.fife.mario.blocks.Block;
import org.fife.mario.blocks.BlockTypes;
import org.fife.mario.enemy.FlyingFish;
import org.fife.mario.sound.SoundEngine;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.*;

/**
 * An "Area" is a single, contiguous part of a level.  Mario can go from one area to the next through pipes.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Area {

    private MapData mapData;
    private SpriteSheet ss;
    private TilesetInfo tilesetInfo;
    private Background background;
    private int music;
    private LinkedList<Animation> anims;
    private List<Character> otherCharacters;
    private List<Character> charsToAdd;
    public List<FireStick> fireSticks;
    private List<Goal> goals;
    private List<FutureTask> futureEvents;
    private Map<Position, WarpInfo> warps;
    private boolean flyingFish;
    public float xOffs, yOffs;
    private Rectangle2D.Float updateBounds;
    private org.newdawn.slick.Animation coinAnim;
    private Random random;
    private boolean water;
    private QuadTree tree;

    private static final float UPDATE_OFFSCREEN_SIZE = 60;

    public Area(MapData data) {

        this.mapData = data;
        otherCharacters = new ArrayList<>();
        charsToAdd = new ArrayList<>();
        fireSticks = new ArrayList<>();
        anims = new LinkedList<>();
        goals = new ArrayList<>();
        futureEvents = new ArrayList<>();
        warps = new HashMap<>();
        background = new Background();
        updateBounds = new Rectangle2D.Float(0, 0,
            640 + 2 * UPDATE_OFFSCREEN_SIZE, 480 + 2 * UPDATE_OFFSCREEN_SIZE);
        coinAnim = Block.createAnimation(BlockTypes.BLOCK_YELLOW_COIN, 300, false);

        xOffs = 0;
        yOffs = getHeight() - 480;
        random = new Random();
        setWater(false);

        tree = new QuadTree(this);
        System.out.println("tree depth: " + tree.getDepth());
    }

    /**
     * Adds a character to the scene.
     *
     * @param c The character to add.
     * @see #removeCharacter(Character)
     */
    public void addCharacter(Character c) {
        charsToAdd.add(c);
    }

    public void addFutureTask(FutureTask task) {
        futureEvents.add(task);
    }

    public void addGoal(Goal goal) {
        goals.add(goal);
    }

    public void addTemporaryAnimation(Animation a) {
        a.setArea(this);
        anims.add(a);
    }

    public void addWarp(Position pos, WarpInfo info) {
        warps.put(pos, info);
    }

    public void centerAround(AbstractEntity entity) {
        float centerX = entity.getCenterX();
        if (centerX < 320) {
            setXScroll(0);
        }
        else if (centerX >= getWidth() - 320) {
            setXScroll(getWidth() - 640);
        }
        else {
            setXScroll(centerX - 320);
        }
    }

    public void checkGettingCoins(Mario mario) {

        mario.getTopLeft(curPoint);
        isCoinAt(curPoint, true);
        mario.getBottomLeft(curPoint2);
        isCoinAt(curPoint2, true);

        if ((curPoint2.y - curPoint.y + 1) > 32 + 2) { // Big Mario can span 3 blocks
            curPoint.y = (curPoint.y + curPoint2.y) / 2;
            isCoinAt(curPoint, true);
        }

        mario.getTopRight(curPoint);
        isCoinAt(curPoint, true);

        mario.getBottomRight(curPoint2);
        isCoinAt(curPoint2, true);

        if ((curPoint2.y - curPoint.y + 1) > 32 + 2) { // Big Mario can span 3 blocks
            curPoint.y = (curPoint.y + curPoint2.y) / 2;
            isCoinAt(curPoint, true);
        }
    }

    private Vector2f curPoint = new Vector2f();
    private Vector2f curPoint2 = new Vector2f();
    private Vector2f[] corners = new Vector2f[4];

    private void getFourCorners(AbstractEntity ch, Vector2f motion) {
        if (corners[0] == null) {
            for (int i = 0; i < corners.length; i++) {
                corners[i] = new Vector2f();
            }
        }
        ch.getTopLeft(corners[0]);
        corners[0].add(motion);
        ch.getTopRight(corners[1]);
        corners[1].add(motion);
        ch.getBottomLeft(corners[2]);
        corners[2].add(motion);
        ch.getBottomRight(corners[3]);
        corners[3].add(motion);
    }

    public CollisionResult checkHittingWall(AbstractEntity ch, Vector2f motion) {

        CollisionResult cr = new CollisionResult();

        if (motion.x == 0 && motion.y == 0) {
            return cr;
        }

        if (motion.y != 0) {
            float temp = motion.x;
            motion.x = 0;
            getFourCorners(ch, motion);
            motion.x = temp;
            if (motion.y < 0) { // Jumping
                if (isSolidTerrainOrBlock(corners[0]) || isSolidTerrainOrBlock(corners[1])) {
                    cr.above = true;
                    int y = (int)corners[0].y;
                    y = (y / 32 + 1) * 32; // Next tile above
                    ch.setY(y - ch.getHitMarginTop());
                }
                else {
                    ch.moveY(motion.y);
                }
            }
            else { // falling
                if (isLandable(corners[2]) || isLandable(corners[3])) {
                    cr.below = true;
                    int y = (int)corners[2].y;
                    y = y / 32 * 32; // Top of tile hit
                    ch.setY(y - ch.getHeight());// + ch.getHitMarginTop());
                }
                else {
                    ch.moveY(motion.y);
                }
            }
        }
        if (motion.x != 0) {
            motion.y = 0;
            getFourCorners(ch, motion);
            boolean hit = false;
            if (motion.x < 0) { // Going left
                if (isSolidTerrainOrBlock(corners[0]) || isSolidTerrainOrBlock(corners[2])) {
                    hit = true;
                }
                else if (ch.getHitBoundsHeight() > 32 + 2) { // Big Mario can span 3 blocks
                    corners[0].y += ch.getHitBoundsHeight() / 2;
                    if (isSolidTerrainOrBlock(corners[0])) {
                        hit = true;
                    }
                }
                if (hit) {
                    cr.leftWall = true;
                    int x = (int)corners[0].x;
                    x = (x / 32 + 1) * 32; // Tile to the left
                    ch.setX(x - ch.getHitMarginX());
                }
                else {
                    ch.moveX(motion.x);
                }
            }
            else { // Going right
                if (isSolidTerrainOrBlock(corners[1]) || isSolidTerrainOrBlock(corners[3])) {
                    hit = true;
                }
                else if (ch.getHitBoundsHeight() > 32 + 2) { // Big Mario can span 3 blocks
                    corners[1].y += ch.getHitBoundsHeight() / 2;
                    if (isSolidTerrainOrBlock(corners[1])) {
                        hit = true;
                    }
                }
                if (hit) {
                    cr.rightWall = true;
                    int x = (int)corners[1].x;
                    x = x / 32 * 32; // Tile to the right
                    ch.setX(x - ch.getWidth() + ch.getHitMarginX());
                }
                else {
                    ch.moveX(motion.x);
                }
            }
        }

        return cr;
    }

    public void checkOtherEntityCollisions(GameContainer container, StateBasedGame game, int delta) {

        OUTER:
        for (int i = 0; i < otherCharacters.size(); i++) {

            Character c = otherCharacters.get(i);

            for (int j = i + 1; j < otherCharacters.size(); j++) {

                Character c2 = otherCharacters.get(j);

                if (c.intersects(c2)) {

                    boolean remove1 = c.collidedWith(c2);
                    boolean remove2 = c2.collidedWith(c);
                    if (remove2) {
                        otherCharacters.remove(j);
                        j--;
                    }
                    if (remove1) {
                        otherCharacters.remove(i);
                        i--;
                        continue OUTER;
                    }
                    if (remove2) {
                        continue;
                    }

                }

            }

        }
    }

    /**
     * Returns the block at a specified position on the world map.
     *
     * @param row The row to check.
     * @param col The column to check.
     * @return The block, or <code>null</code> if there is none there.
     */
    public Block getBlockAt(int row, int col) {
        if (row >= 0 && row < mapData.getRowCount() &&
            col >= 0 && col < mapData.getColumnCount()) { // Mario may have jumped "off" the screen
            return mapData.getBlockAt(row, col);
        }
        return null;
    }

    /**
     * Returns the block at the specified position in the view.
     *
     * @param p The position in the view.
     * @return The block, or <code>null</code> if there is none there.
     */
    private Block getBlockAt(Vector2f p) {
        int row = viewToRow(p.y);
        int col = viewToCol(p.x);
        return getBlockAt(row, col);
    }

    public int getColumnCount() {
        return mapData.getColumnCount();
    }

    public float getHeight() {
        return mapData.getRowCount() * 32;
    }

    public int getMusic() {
        return music;
    }

    public List<Character> getOtherCharacters() {
        return otherCharacters;
    }

    public int getRowCount() {
        return mapData.getRowCount();
    }

    public int getTerrainAt(int layer, int row, int col) {
        return mapData.getTerrain(layer, row, col);
    }

    /**
     * Returns the terrain at the specified point.
     *
     * @param p The point.
     * @return The terrain.
     * @see #isCoinAt(Vector2f, boolean)
     */
    private int getTerrainAt(Vector2f p) {
        int row = viewToRow(p.y);
        int col = viewToCol(p.x);
        if (row >= 0 && row < mapData.getRowCount() &&
            col >= 0 && col < mapData.getColumnCount()) { // Mario may have jumped "off" the screen
            return mapData.getTerrain(Constants.MIDDLE, row, col);
        }
        return 0;
    }

    public float getWidth() {
        return mapData.getColumnCount() * 32;
    }

    public boolean isAtAnEdge(Character ch) {
        if (ch.getDirection() == Character.LEFT) {
            ch.getBottomLeft(curPoint);
        }
        else {
            ch.getBottomRight(curPoint);
        }
        curPoint.y++;
        int terrain = getTerrainAt(curPoint);
        if (terrain > 0) {
            return false;
        }
        Block b = getBlockAt(curPoint);
        if (b != null && b.isHittable() && !b.isHidden()) {
            return false;
        }
        return true;
    }

    /**
     * Returns whether there is a coin at the specified location on the map.
     *
     * @param p      The location.
     * @param remove Whether to remove the coin, if it exists.
     * @return Whether there is (was) a coin there.
     * @see #getTerrainAt(Vector2f)
     */
    private boolean isCoinAt(Vector2f p, boolean remove) {
        boolean coin = false;
        int col = viewToCol(p.x);
        int row = viewToRow(p.y);
        if (row >= 0 && row < mapData.getRowCount() &&
            col >= 0 && col < mapData.getColumnCount()) { // Mario may have jumped "off" the screen
            coin = mapData.isCoinAt(row, col, remove);
            if (coin) {
                float x = p.x - (((int)p.x) % 32);
                float y = p.y - (((int)p.y) % 32);
                addTemporaryAnimation(new CoinGrabbedAnimation(x, y, false));
                PlayerInfo.get(0).incCoinCount(1);
                SoundEngine.get().play(SoundEngine.SOUND_COIN);
            }
        }
        return coin;
    }

    public boolean isGoalReached(Mario mario) {
        for (Goal g : goals) { // Usually just 1
            if (g.intersects(mario)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPipeSide(int row, int col) {
        if (row < 0 || row >= mapData.getRowCount() || col < 0 || col >= mapData.getColumnCount()) {
            return false;
        }
        return mapData.isPipeSide(row, col);
    }

    /**
     * Returns whether the specified terrain is completely solid (e.g. an entity can not enter it).
     *
     * @param row The row to check.
     * @param col The column to check.
     * @return Whether the terrain at the specified cell is solid.
     */
    public boolean isSolidTerrainOrBlock(int row, int col) {
        if (row < 0 || row >= getRowCount() || col < 0 || col >= getColumnCount()) {
            return false;
        }
        int terrain = getTerrainAt(Constants.MIDDLE, row, col);
        if (terrain > 0 && tilesetInfo.isSolid(terrain - 1)) {
            return true;
        }
        Block b = getBlockAt(row, col);
        return b != null && b.isHittable() && !b.isHidden();
    }

    /**
     * Returns whether the specified terrain is completely solid (e.g. an entity can not enter it).
     *
     * @param p The location to check.
     * @return Whether the terrain at the specified point is solid.
     */
    public boolean isSolidTerrainOrBlock(Vector2f p) {
        int row = viewToRow(p.y);
        int col = viewToCol(p.x);
        return isSolidTerrainOrBlock(row, col);
    }

    public boolean isLandable(Vector2f p) {

        int row = viewToRow(p.y);
        int col = viewToCol(p.x);

        if (row < 0 || row >= mapData.getRowCount() || col < 0 || col >= mapData.getColumnCount()) {
            // Mario jumped "off" the screen
            return false;
        }

        boolean landable = isSolidTerrainOrBlock(row, col);
        if (!landable) {
            int terrain = getTerrainAt(Constants.MIDDLE, row, col);
            landable = terrain > 0 && tilesetInfo.isLandable(terrain - 1);
        }
        return landable;
    }

    public boolean isInUpdateBounds(Character c) {

        Rectangle2D.Float charBounds = c.getCoreBounds();
        // Keep updating stuff a little behind us.
        //updateBounds.setRect(xOffs-60, yOffs-60, xOffs+640, yOffs+480+60);
        final float fudgeFactor = 60;
        updateBounds.setRect(xOffs - fudgeFactor, -fudgeFactor, xOffs + 640, fudgeFactor + getHeight());

        boolean res;
        if (c.getHeight() != 0) { // Most stuff
            res = charBounds.intersects(updateBounds);
        }
        else { // Piranha plants inside pipes
            res = updateBounds.contains(c.getX(), c.getY()) ||
                updateBounds.contains(c.getX() + c.getWidth() - 1, c.getY());
        }

        return res;

    }

    public boolean isTerrainAbove(AbstractEntity ch) {

        ch.getTopRight(curPoint);
        curPoint.y--;
        boolean terrainAbove = isTerrainAboveImpl(ch, curPoint);
        if (!terrainAbove) {
            ch.getTopLeft(curPoint);
            curPoint.y--;
            terrainAbove = isTerrainAboveImpl(ch, curPoint);
        }

        return terrainAbove;

    }

    private boolean isTerrainAboveImpl(AbstractEntity ch, Vector2f point) {

        boolean hit = false;

        int terrain = getTerrainAt(point);
        if (terrain > 0) {
            terrain--;
            if (tilesetInfo.isSolid(terrain)) {
                hit = true;
            }
        }
        else {
            Block b = getBlockAt(point);
            if (b != null) {
                if (ch instanceof Mario && ((Mario)ch).getYSpeed() < 0) {
                    Mario mario = (Mario)ch;
                    hit = possiblyActivateBlockAt(mario, curPoint) > 0;
                }
            }
        }

        if (hit && (ch instanceof Mario)) {
            SoundEngine.get().play(SoundEngine.SOUND_HIT_HEAD);
        }

        return hit;

    }

    public boolean isTerrainBelow(AbstractEntity ch) {

        // HACK to make this check only in one place.
        if (ch.getStandingOn() != null) {
            return true;
        }

        ch.getBottomLeft(curPoint);
        curPoint.y++;
        if (isTerrainBelowPoint(curPoint)) {
            return true;
        }

        ch.getBottomRight(curPoint);
        curPoint.y++;
        return isTerrainBelowPoint(curPoint);

    }

    private boolean isTerrainBelowPoint(Vector2f p) {
        if (((int)p.y) % 32 == 0) { // Only check if just above.
            int terrain = getTerrainAt(p);
            if (terrain > 0 && tilesetInfo.isLandable(terrain - 1)) {
                return true;
            }
            Block b = getBlockAt(p);
            if (b != null && b.isHittable() && !b.isHidden()) {
                return true;
            }
        }
        return false;
    }

    public boolean isTouchingLava(Character ch) {
        ch.getBottomLeft(tempVec);
        int row = viewToRow(tempVec.y);
        int col = viewToCol(tempVec.x);
        if (mapData.isLava(row, col)) {
            return true;
        }
        ch.getBottomRight(tempVec);
        row = viewToRow(tempVec.y);
        col = viewToCol(tempVec.x);
        return mapData.isLava(row, col);
    }

    private Vector2f tempVec = new Vector2f();
    private Position warpCheckPos = new Position();

    public WarpInfo isWarpAbovePoint(Vector2f p) {
        WarpInfo info = null;
        if (((int)(p.y - 1)) % 32 == 31) { // Only check if just above.
            int row = viewToRow(p.y - 1);
            int col = viewToCol(p.x);
            warpCheckPos.set(row, col);
            info = warps.get(warpCheckPos);
        }
        return info;
    }

    public WarpInfo isWarpBelowPoint(Vector2f p) {
        WarpInfo info = null;
        if (((int)(p.y + 1)) % 32 == 0) { // Only check if just above.
            int row = viewToRow(p.y + 1);
            int col = viewToCol(p.x);
            warpCheckPos.set(row, col);
            info = warps.get(warpCheckPos);
        }
        return info;
    }

    public WarpInfo isWarpRightOfPoint(Vector2f p) {
        WarpInfo info = null;
        if (((int)(p.x + 1)) % 32 == 0) { // Only check if just beside.
            int row = viewToRow(p.y);
            int col = viewToCol(p.x + 1);
            warpCheckPos.set(row, col);
            info = warps.get(warpCheckPos);
        }
        return info;
    }

    public WarpInfo isWarpLeftOfPoint(Vector2f p) {
        WarpInfo info = null;
        if (((int)p.x) % 32 == 0) { // Only check if just beside.
            int row = viewToRow(p.y);
            int col = viewToCol(p.x - 1);
            warpCheckPos.set(row, col);
            info = warps.get(warpCheckPos);
        }
        return info;
    }

    /**
     * Returns whether this is a water area.
     *
     * @return Whether this is a water area.
     * @see #setWater(boolean)
     */
    public boolean isWater() {
        return water;
    }

    /**
     * Called when Mario leaves this <code>Area</code> for another one.
     */
    public void marioLeft() {
        for (Character ch : otherCharacters) {
            ch.reset();
        }
    }

    private int possiblyActivateBlockAt(Mario mario, Vector2f p) {
        int fallDown = 0; // 0==false, 1,2==true
        Block b = getBlockAt(p);
        if (b != null && b.isHittable()) {
            fallDown = 1;
            //playSound(b.getSound());
            if (b.isActivatable()) {
                b.activate(mario);
                fallDown = 2;
            }
        }
        return fallDown;
    }

    /**
     * Removes the block at the specified location.
     *
     * @param row The row of the block.
     * @param col The column of the block.
     * @return Whether a block was at that position to remove.
     */
    public boolean removeBlockAt(int row, int col) {
        return mapData.removeBlockAt(row, col);
    }

    /**
     * Removes a character from this level.
     *
     * @param c The character to remove.
     * @see #addCharacter(Character)
     */
    public void removeCharacter(Character c) {
        otherCharacters.remove(c);
    }

    public void render(GameContainer container, StateBasedGame game, Graphics g,
                       Color filter) throws SlickException {

        if (filter == null) {
            filter = Color.white;
        }

        background.render(container, game, g, filter);
        renderTiles(container, game, g, filter);
        if (GameInfo.get().getLevel().getShowGrid()) {
            renderGrid(container, game, g);
        }

        for (Goal goal : goals) {
            goal.renderLeft(container, game, g, filter);
        }

        for (AbstractEntity e : otherCharacters) {
            e.render(container, game, g, filter);
        }

        for (Animation a : anims) {
            a.render(container, game, g, filter);
        }

        for (FireStick fs : fireSticks) {
            fs.render(container, game, g, filter);
        }
    }

    /**
     * Renders any foreground terrain.
     *
     * @param container
     * @param game
     * @param g
     */
    public void renderForeground(GameContainer container, StateBasedGame game,
                                 Graphics g, Color filter) throws SlickException {

        int startCol = ((int)xOffs) / 32;
        float startX = (startCol * 32) - xOffs;
        float x = startX;
        int col = startCol;

        int startRow = ((int)yOffs) / 32;
        float startY = (startRow * 32) - yOffs;
        float y = startY;
        int row = startRow;

        int ssColCount = ss.getHorizontalCount();
        while (y < 480) {
            while (x < 640) {

                int terrain = mapData.getTerrain(Constants.FOREGROUND, row, col);
                if (terrain > 0) {
                    terrain--;
                    Image img = ss.getSubImage(terrain % ssColCount, terrain / ssColCount);
                    g.drawImage(img, x, y, filter);
                }

                col++;
                x += 32;

            }
            col = startCol;
            x = startX;
            row++;
            y += 32;
        }

        for (Goal goal : goals) {
            goal.renderTarget(container, game, g, filter);
            goal.renderRight(container, game, g, filter);
        }
    }

    /**
     * Renders a grid to visually see where tiles are divided.
     *
     * @param container
     * @param game
     * @param g
     */
    public void renderGrid(GameContainer container, StateBasedGame game,
                           Graphics g) {

        g.setColor(Color.red);

        float x = 32 - (xOffs % 32);
        while (x < container.getWidth()) {
            g.drawLine(x, 0, x, container.getHeight());
            x += 32;
        }

        float y = 32 - (yOffs % 32);
        while (y < container.getHeight()) {
            g.drawLine(0, y, container.getWidth(), y);
            y += 32;
        }
    }

    private int animatedLavaFrame;
    private int lavaDelta;

    /**
     * Renders all visible tiles, blocks and coins.
     *
     * @param container
     * @param game
     * @param g
     * @return The first visible column.
     */
    public int renderTiles(GameContainer container, StateBasedGame game,
                           Graphics g, Color filter) throws SlickException {

//		float y = bg.getHeight() - container.getHeight();
        int startCol = ((int)xOffs) / 32;
        float startX = (startCol * 32) - xOffs;
        float x = startX;
        int col = startCol;

        int startRow = ((int)yOffs) / 32;
        float startY = (startRow * 32) - yOffs;
        float y = startY;
        int row = startRow;
        int ssColCount = ss.getHorizontalCount();
        while (y < 480) {
            while (x < 640) {

                int terrain = mapData.getTerrain(Constants.BACKGROUND, row, col);
                if (terrain > 0) {
                    terrain--;
                    Image img = ss.getSubImage(terrain % ssColCount, terrain / ssColCount);
                    g.drawImage(img, x, y, filter);
                }

                terrain = mapData.getTerrain(Constants.MIDDLE, row, col);
                if (terrain > 0) {
                    terrain--;
                    // TODO: Move this "animated tile" logic into MapData/MapCell
                    if (terrain >= 41 && terrain <= 44) {
                        terrain += animatedLavaFrame;
                        if (terrain > 44) {
                            terrain -= 4;
                        }
                    }
                    Image img = ss.getSubImage(terrain % ssColCount, terrain / ssColCount);
                    g.drawImage(img, x, y, filter);
                }

                if (mapData.isCoinAt(row, col, false)) {
                    coinAnim.draw(x, y);
                }
                else {
                    Block b = mapData.getBlockAt(row, col);
                    if (b != null) {
                        b.render(container, game, g, filter);
                    }
                }

                col++;
                x += 32;

            }
            col = startCol;
            x = startX;
            row++;
            y += 32;
        }

        return startCol;
    }

    public void setBackgroundImage(String imgName) throws IOException {
        background.setImage(imgName);
    }

    public void setFlyingFish(boolean flyingFish) {
        this.flyingFish = flyingFish;
    }

    public void setMusic(int music) {
        this.music = music;
    }

    public void setTerrainAt(Vector2f p, int terrain) {
        int row = viewToRow(p.y);
        int col = viewToCol(p.x);
        if (row >= 0 && row < mapData.getRowCount() &&
            col >= 0 && col < mapData.getColumnCount()) {
            mapData.setTerrain(Constants.MIDDLE, row, col, terrain);
        }
    }

    public void setTileset(String tileset) throws SlickException {
        String fileName = "img/" + tileset + ".png";
        Image img = new Image(fileName, false, Image.FILTER_NEAREST, Color.red);
        ss = new SpriteSheet(img, 32, 32, 2);
        tilesetInfo = TilesetInfo.getInfo(tileset);
    }

    /**
     * Sets whether this is a water area.
     *
     * @param water Whether this is a water area.
     * @see #isWater()
     */
    public void setWater(boolean water) {
        this.water = water;
    }

    /**
     * Scrolls the level to the right.
     *
     * @param xOffs The number of pixels to scroll to the right.
     * @see #setYScroll(float)
     */
    public void setXScroll(float xOffs) {
        this.xOffs = xOffs;
        updateBounds.x = xOffs - UPDATE_OFFSCREEN_SIZE;
    }

    /**
     * Scrolls the level to the right.
     *
     * @param yOffs The number of pixels to scroll down.
     * @see #setXScroll(float)
     */
    public void setYScroll(float yOffs) {
        this.yOffs = yOffs;
        updateBounds.y = yOffs - UPDATE_OFFSCREEN_SIZE;
    }

    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {

        lavaDelta += delta;
        if (lavaDelta >= 150) {
            lavaDelta = 0;
            animatedLavaFrame++;
            if (animatedLavaFrame >= 4) {
                animatedLavaFrame = 0;
            }
        }

        background.setOffset(xOffs, yOffs);
        background.update(container, game, delta);

        for (int i = 0; i < futureEvents.size(); i++) {
            FutureTask task = futureEvents.get(i);
            if (task.decreaseDelay(delta)) {
                task.run();
                futureEvents.remove(i);
                i--;
            }
        }

        Main main = (Main)game;
        Mario mario = main.getMario();
        if (mario.isChangingSize()) {
            return;
        }

        // Add any new characters from the previous frame.
        for (Iterator<Character> i = charsToAdd.iterator(); i.hasNext();) {
            Character c = i.next();
            if (isInUpdateBounds(c)) {
                c.setArea(this);
                otherCharacters.add(c);
                i.remove();
            }
        }

        // Possibly launch a flying fish
        if (flyingFish) {
            if (random.nextInt(150) == 0) {
                int range = 320 - 20;
                float x = xOffs + 320 + random.nextInt(range);
                FlyingFish ff = new FlyingFish(this, x, yOffs + 480);
                addCharacter(ff);
            }
        }

        // Update any "shared" animations.
        coinAnim.update(delta);

        // Update any temporary animations
        List<Animation> newAnims = null;
        for (Iterator<Animation> i = anims.iterator(); i.hasNext();) {
            Animation a = i.next();
            a.update(container, game, delta);
            if (a.isDone()) {
                List<Animation> l = a.getReplacementAnimations();
                if (l != null) {
                    if (newAnims == null) {
                        newAnims = new ArrayList<>();
                    }
                    newAnims.addAll(l);
                }
                Character replacement = a.dispose(mario);
                if (replacement != null) {
                    otherCharacters.add(replacement);
                }
                i.remove();
            }
        }
        if (newAnims != null) {
            for (Animation newAnim : newAnims) {
                // Current area may have changed if Mario went through a pipe
                Area area = GameInfo.get().getLevel().getCurrentArea();
                area.addTemporaryAnimation(newAnim);
            }
        }

        // Update any visible blocks.
        //float y = bg.getHeight() - container.getHeight();
        int startCol = ((int)xOffs) / 32;
        int endCol = startCol + 640 / 32;
        int startRow = ((int)yOffs) / 32;
        int endRow = startRow + 480 / 32;
        for (int row = startRow; row < endRow; row++) {
            for (int col = startCol; col < endCol; col++) {
                Block block = mapData.getBlockAt(row, col);
                if (block != null) {
                    mapData.getBlockAt(row, col).update(container, game, delta);
                }
            }
        }

        // Update any other entities.
        int updatedCharCount = 0;
        int totalCharCount = 0;
        //for (Character c : otherCharacters) {
        for (Iterator<Character> i = otherCharacters.iterator(); i.hasNext();) {
            Character c = i.next();
            totalCharCount++;
            if (isInUpdateBounds(c)) {
                c.update(container, game, delta);
                if (c.isDone()) {
                    System.out.println("Removing: " + c);
                    i.remove();
                }
                updatedCharCount++;
            }
        }
        //System.out.println("Updated characters: " + updatedCharCount + "/" + totalCharCount);

        // Update goals
        for (Goal goal : goals) {
            goal.update(container, game, delta);
        }

        for (FireStick fs : fireSticks) {
            fs.update(container, game, delta);
        }
    }

    public final int viewToCol(float x) {
        return ((int)x) / 32;
    }

    public final int viewToRow(float y) {
        return ((int)y) / 32;
    }
}
