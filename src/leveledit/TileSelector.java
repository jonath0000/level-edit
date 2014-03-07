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

	public JList tileList;

	public TileSelector() {
		super();
		tileList = new JList();
		this.setViewportView(tileList);
	}

	public void setTiles(ImageIcon tilePic, int nTiles, int sourceWidth, int sourceHeight, int representationWidth, int representationHeight) {

		Image tiles = tilePic.getImage();
		ImageIcon listitems[] = new ImageIcon[nTiles];

		for (int i = 0; i < nTiles; i++) {

			int row = i / Config.tilesPerRow;
			int tile = i % Config.tilesPerRow;

			BufferedImage newIm = new BufferedImage(representationWidth, representationHeight, BufferedImage.TYPE_3BYTE_BGR);
			Graphics g = newIm.getGraphics();
			g.drawImage(tiles, 0, 0, representationWidth, representationHeight, tile * sourceWidth, row * sourceHeight, (tile + 1)
					* sourceWidth, (row + 1) * sourceHeight, this);
			ImageIcon icon = new ImageIcon(newIm);
			listitems[i] = icon;
		}

		tileList.setLayoutOrientation(JList.VERTICAL_WRAP);
		tileList.setVisibleRowCount(8);
		setPreferredSize(new Dimension(representationWidth * 8, representationHeight * 8));
		tileList.setListData(listitems);
	}

	public int getSelectedIndex() {
		return tileList.getSelectedIndex();
	}

	public void setSelectedIndex(int n) {
		tileList.setSelectedIndex(n);
	}
}
