package leveledit;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JScrollPane;

/**
 * Frame allowing user to choose a tile.
 * 
 */
public class TileSelector extends JScrollPane {

	public JList<ImageIcon> tileList;

	public TileSelector() {
		super();
		tileList = new JList<ImageIcon>();
		this.setViewportView(tileList);
	}

	public void setTiles(ImageIcon tilePic, int nTiles, int w, int h) {

		Image tiles = tilePic.getImage();
		ImageIcon listitems[] = new ImageIcon[nTiles];

		for (int i = 0; i < nTiles; i++) {

			int row = i / Config.tilesPerRow;
			int tile = i % Config.tilesPerRow;

			BufferedImage newIm = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
			Graphics g = newIm.getGraphics();
			g.drawImage(tiles, 0, 0, w, h, tile * w, row * h, (tile + 1) * w, (row + 1) * h, this);
			ImageIcon icon = new ImageIcon(newIm);
			listitems[i] = icon;
		}
		
		tileList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		tileList.setVisibleRowCount(8);
		setPreferredSize(new Dimension(w * 8, h * 8));
		tileList.setListData(listitems);	
	}

	public int getSelectedIndex() {
		return tileList.getSelectedIndex();
	}

	public void setSelectedIndex(int n) {
		tileList.setSelectedIndex(n);
	}
}
