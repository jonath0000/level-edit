package leveledit;


import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import tools.RectSelecter;


public class ApplicationClipBoard implements ClipboardOwner, RectSelecter {

	private int selectX;
	private int selectY;
	private int selectW;
	private int selectH;
	private boolean hasSelection = false;
	private FloatingLayer floatingLayer = null;
	private CurrentLevelProvider currentLevelProvider;

	public ApplicationClipBoard(CurrentLevelProvider currentLevelProvider) {
		this.currentLevelProvider = currentLevelProvider;
	}
	
	private void deleteSelectedImageInternal() {
		if (!hasSelection) return;
		currentLevelProvider.getLevel().getTileMap().deleteTiles(selectX, selectY, 
				selectW, selectH, currentLevelProvider.getSelectedLayer());
	}
	
	public void deleteSelectedImage() {
		deleteSelectedImageInternal();
		hasSelection = false;
	}

	public void cutSelectedImage() {
		if (!hasSelection) return;
		copySelectedImageInternal();
		deleteSelectedImageInternal();
		hasSelection = false;
	}

	private void copySelectedImageInternal() {
		if (!hasSelection) return;
		int[][] tiles = currentLevelProvider.getLevel().getTileMap().getSubTiles(selectX, selectY, 
				selectW, selectH, currentLevelProvider.getSelectedLayer());
		if (tiles == null) return;
		TransferableTileMap clipBoardImage = new TransferableTileMap(tiles);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(clipBoardImage, this);
	}
	
	public void copySelectedImage() {
		copySelectedImageInternal();
		hasSelection = false;
	}
	
	public void pasteImage() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Object transferObject;
		try {
			transferObject = clipboard.getContents(null).getTransferData(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType));
		} catch (Exception e) {
			return;
		}
		if (!(transferObject instanceof int[][])) return;
		floatingLayer = new FloatingLayer((int[][])transferObject, selectX, selectY);
		hasSelection = false;
	}
	
	public void selectAll() {
		selectX = 0;
		selectY = 0;
		selectW = currentLevelProvider.getLevel().getTileMap().getWidth();
		selectH = currentLevelProvider.getLevel().getTileMap().getHeight();
		hasSelection = true;
	}
	
	public void selectNone() {
		hasSelection = false;
	}

	/**
	 * Indicate that there is an active selection, that should be
	 * drawn at the getSelection*() coordinates.
	 * @return True if selection.
	 */
	public boolean hasSelection() {
		return hasSelection;
	}

	public int getSelectionX() {
		return selectX;
	}

	public int getSelectionY() {
		return selectY;
	}

	public int getSelectionWidth() {
		return selectW;
	}

	public int getSelectionHeight() {
		return selectH;
	}
	
	/**
	 * Indicate that there is a floating layer, that should be drawn
	 * on top of the regular image, at coordinates x,y.
	 * @return
	 */
	public boolean hasFloatingLayer() {
		if (floatingLayer == null) return false;
		return true;
	}

	/**
	 * Apply the floating layer to the image.
	 */
	public void anchorFloatingLayer() {
		if (floatingLayer == null)
			return;
		currentLevelProvider.getLevel().getTileMap().insertTiles(
				floatingLayer.getPosX(), floatingLayer.getPosY(), floatingLayer.getTiles(), 
				currentLevelProvider.getSelectedLayer());
		floatingLayer = null;
	}

	public FloatingLayer getFloatingLayer() {
		return floatingLayer;
	}
	
	@Override
	public void setSelectedRect(int x, int y, int width, int height) {
		if (hasFloatingLayer()) return;
		hasSelection = true;
		selectX = x;
		selectY = y;
		selectW = width;
		selectH = height;
	}

	@Override
	public void lostOwnership(Clipboard arg0, Transferable arg1) {
	}
}
