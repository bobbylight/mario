package org.fife.mario.editor;

import org.fife.mario.blocks.BlockTypes;

/**
 * Information about a block.
 */
class BlockInfo {

	public static final int CONTENT_NONE			= -1;
	public static final int CONTENT_COINS_1			= 0;
	public static final int CONTENT_COINS_10		= 1;
	public static final int CONTENT_FIRE_FLOWER		= 2;
	public static final int CONTENT_ONE_UP			= 3;
	public static final int CONTENT_STAR			= 4;

	private BlockTypes type;
	private String text; // Only valid if type==BLOCK_INFORMATION
	private int content;
	private boolean hidden;

	private static final String DEFAULT_TEXT		= "Change my text!";


	BlockInfo(BlockTypes type) {
		setType(type);
		this.content = getDefaultContent(type);
		hidden = false;
	}


	BlockInfo(BlockTypes type, int content) {
		setType(type);
		if (type.getCanHaveContent() && isValidContent(content)) {
			this.content = content;
		}
		else {
			this.content = getDefaultContent(type);
		}
	}


	public int getContent() {
		return content;
	}


	public static int getDefaultContent(BlockTypes blockType) {
		switch (blockType) {
			default:
			case BLOCK_BLUE:
			case BLOCK_GRAY:
			case BLOCK_MUSIC_NOTE:
				return CONTENT_NONE;
			case BLOCK_QUESTION:
			case BLOCK_QUESTION_RED:
				return CONTENT_COINS_1;
			case BLOCK_YELLOW:
				return CONTENT_NONE;
		}
	}


	public String getText() {
		return text;
	}


	public BlockTypes getType() {
		return type;
	}


	public boolean isHidden() {
		return hidden;
	}


	private static boolean isValidContent(int content) {
		return content>=CONTENT_NONE && content<=CONTENT_STAR;
	}


	public void setContent(int content) {
		this.content = content;
	}


	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}


	public void setText(String text) {
		this.text = text;
	}


	private void setType(BlockTypes type) {
		if (type!=null && type!=this.type) {
			this.type = type;
			if (type==BlockTypes.BLOCK_INFORMATION) {
				this.text = DEFAULT_TEXT;
			}
		}
	}


	public boolean toggleHidden() {
		return hidden = !hidden;
	}


	@Override
	public String toString() {
		return "[BlockInfo: type==" + getType() +
				", content==" + getContent() +
				", text==" + getText() + "]";
	}


}
